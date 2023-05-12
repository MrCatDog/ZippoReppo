package com.example.zipporeppogithub.ui.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.ErrorEntity
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.ResultWrapper
import com.example.zipporeppogithub.model.db.HistoryRecord
import com.example.zipporeppogithub.model.network.GithubRepo
import com.example.zipporeppogithub.utils.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReposViewModel
@AssistedInject constructor(
    private val repository: Repository,
    @Assisted("user") private val userLogin: String,
    @Assisted("path") private val downloadPath: String
) : ViewModel() {

    companion object {
        const val FILE_EXTENSION = ".zip"
        const val BUFF_SIZE = 4096
    }

    private val reducer = ReposReducer(ReposState.initial())
    val uiState: StateFlow<ReposState>
        get() = reducer.state

    private var request: Job? = null
    private var resultsPage: Int = 1
    private var isAllDownloaded = false
    private val reposToDownload = ArrayList<String>()
    private var isAllDownloadRepoWasShown = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getRepos(userLogin)
        }
    }

    private suspend fun searchRepos(query: String): ResultWrapper<List<GithubRepo>> =
        repository.loadUserRepos(query, REPOS_RESULT_COUNT, resultsPage)

    private suspend fun getRepos(query: String) {
        reducer.sendEvent(ReposEvent.ReposLoading)
        when (val answer = searchRepos(query)) {
            is ResultWrapper.Success -> {
                val repos = answer.value
                if (repos.isNotEmpty()) {
                    reducer.sendEvent(ReposEvent.NewReposFound(repos))
                } else {
                    isAllDownloaded = true
                    reducer.sendEvent(ReposEvent.NoReposFound)
                }
            }
            is ResultWrapper.Failure -> {
                val errMsg = handleError(answer.error)
                if (errMsg != null) {
                    reducer.sendEvent(ReposEvent.SetError(errMsg))
                }
            }
        }
    }

    fun linkBtnClicked(item: GithubRepo) {
        reducer.sendEvent(ReposEvent.NavigateToHtml(item.url))
    }

    fun downloadBtnClicked(item: GithubRepo) {
        reposToDownload.add(item.name)
        if (!uiState.value.isPermissionRequired) {
            reducer.sendEvent(ReposEvent.PermissionRequested(true))
        }
    }

    fun setPermissionAnswer(answer: Map<String, Boolean>) {
        reducer.sendEvent(ReposEvent.PermissionRequested(false))
        var isAllGranted = true
        for (isGranted in answer.values) {
            isAllGranted = isAllGranted && isGranted
        }
        if (isAllGranted) {
            //Возможно ConcurrentModificationException, если подобный вызов не блокирует содержимое reposToDownload
            val localReposToDownload = buildList { addAll(reposToDownload) }
            reposToDownload.clear()
            viewModelScope.launch(Dispatchers.IO) {
                localReposToDownload.forEach { loadZipRepo(it) }
            }
        } else {
            reposToDownload.clear()
            reducer.sendEvent(ReposEvent.ShowSnack(R.string.no_permission_error_text))
        }
    }

    private suspend fun saveDownloadHistoryRecord(repoName: String, dateTime: String) {
        repository.saveDownloadInHistory(
            HistoryRecord(
                userLogin,
                repoName,
                dateTime
            )
        )
    }

    private suspend fun loadZipRepo(repoName: String) {
        when (val answer = repository.loadRepoZip(userLogin, repoName)) {
            is ResultWrapper.Success -> {
                saveFile(answer.value, repoName, downloadPath)
            }
            is ResultWrapper.Failure -> {
                val errMsg = handleError(answer.error)
                if (errMsg != null) {
                    reducer.sendEvent(ReposEvent.SetError(errMsg))
                }
            }
        }
    }

    private suspend fun saveFile(body: ResponseBody, repoName: String, path: String) {
        val dateTime: String =
            SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Calendar.getInstance().time)

        when (val answer = repository.saveFileInExternalStorage(
            body,
            "$path/$repoName-$userLogin-$dateTime$FILE_EXTENSION"
        )) {
            is ResultWrapper.Success -> {
                saveDownloadHistoryRecord(repoName, dateTime)
                reducer.sendEvent(ReposEvent.ShowSnack(R.string.file_downlod_succes))
            }
            is ResultWrapper.Failure -> {
                val errMsgResInt = handleError(answer.error)
                if (errMsgResInt != null) {
                    reducer.sendEvent(ReposEvent.SetError(errMsgResInt))
                }
            }
        }
    }

    fun retryBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            getRepos(userLogin)
        }
    }

    fun screenNavigateOut() {
        reducer.sendEvent(ReposEvent.ScreenNavigateOut)
    }

    fun snackShown() {
        reducer.sendEvent(ReposEvent.SnackWasShown)
    }

    fun onScrolledToEnd() {
        if (request?.isActive == true || isAllDownloaded) {
            if (isAllDownloaded && !isAllDownloadRepoWasShown) {
                reducer.sendEvent(ReposEvent.ShowSnack(R.string.all_repos_download_text))
                isAllDownloadRepoWasShown = true
            }
        } else {
            resultsPage++
            request = viewModelScope.launch(Dispatchers.IO) {
                getRepos(userLogin)
            }
        }
    }

    private fun handleError(error: ErrorEntity): Int? {
        return when (error) {
            is ErrorEntity.ApiError -> when (error) {
                ErrorEntity.ApiError.Network -> R.string.network_error_text
                ErrorEntity.ApiError.NotFound -> R.string.not_found_error_text
                ErrorEntity.ApiError.AccessDenied -> R.string.access_denied_error_text
                ErrorEntity.ApiError.ServiceUnavailable -> R.string.service_unavailable_error_text
            }
            is ErrorEntity.DBError -> when (error) {
                ErrorEntity.DBError.NoPermission -> R.string.no_permission_error_text
                ErrorEntity.DBError.Common -> R.string.common_db_error_text
            }
            is ErrorEntity.Cancel -> null
            is ErrorEntity.ExtError -> when (error) {
                ErrorEntity.ExtError.Permission -> R.string.no_permission_error_text
                ErrorEntity.ExtError.Common -> R.string.file_download_error
            }
            is ErrorEntity.UnknownError -> R.string.unknown_error_text
        }
    }
}

@AssistedFactory
interface ReposViewModelFactory {
    fun create(
        @Assisted("user") userLogin: String,
        @Assisted("path") downloadPath: String
    ): ReposViewModel
}
