package com.example.zipporeppogithub.ui.repos

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.*


class ReposViewModel
@AssistedInject constructor(
    private val repository: Repository,
    @Assisted("user") private val userLogin: String,
    @Assisted("path") private val downloadPath: String
) : ViewModel() {

    companion object {
        const val VISIBLE_THRESHOLD = 5
        const val FILE_EXTENSION = ".zip"
        const val BUFF_SIZE = 4096
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _repos = MutableLiveData<List<GithubRepo>>()
    val repos: LiveData<List<GithubRepo>>
        get() = _repos

    private val _additionalRepos = MutableLiveData<List<GithubRepo>>()
    val additionalRepos: LiveData<List<GithubRepo>>
        get() = _additionalRepos

    private val _message = MutableLiveEvent<Int?>()
    val message: LiveData<Int?>
        get() = _message

    private val _snackMessage = MutableLiveEvent<Int>()
    val snackMessage: LiveData<Int>
        get() = _snackMessage

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean>
        get() = _isError

    private val _htmlUrl = MutableLiveEvent<String>()
    val url: LiveData<String>
        get() = _htmlUrl

    private val _isPermissionRequested = MutableLiveEvent<Boolean>()
    val isPermissionRequested: LiveData<Boolean>
        get() = _isPermissionRequested

    private var request: Job? = null
    private var resultsPage: Int = 1
    private var allDownloaded = false
    private var permissions = false

    init {
        viewModelScope.launch(Dispatchers.IO) { searchNew(userLogin) }
    }

    private suspend fun searchRepos(query: String): ResultWrapper<List<GithubRepo>> =
        repository.loadUserRepos(query, REPOS_RESULT_COUNT, resultsPage)

    private suspend fun searchNew(query: String) {
        _message.postValue(null)
        _isError.postValue(false)
        _isLoading.postValue(true)
        when (val answer = searchRepos(query)) {
            is ResultWrapper.Success -> {
                val repos = answer.value
                _repos.postValue(
                    repos.ifEmpty {
                        _message.postValue(R.string.empty_repos_text)
                        emptyList()
                    }
                )
            }
            is ResultWrapper.Failure -> {
                val errMsg = handleError(answer.error)
                if (errMsg != null) {
                    _message.postValue(errMsg)
                    _isError.postValue(true)
                }
            }
        }
        _isLoading.postValue(false)
    }

    fun downloadBtnClicked(item: GithubRepo, pos: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isPermissionRequested.postValue(true)
            loadZipRepo(item.name)
        }
    }

    fun setPermissionAnswer(answer: Boolean) {
        permissions = answer
    }

    private suspend fun saveDownloadHistoryRecord(repoName: String, dateTime: String) {
        repository.saveDownloadInHistory(
            HistoryRecord(
                userLogin, repoName,
                dateTime
            )
        )
    }

    //Suppress ???? _snackMessage.postValue(errMsg) - ?????? ??????????.
    @SuppressLint("NullSafeMutableLiveData")
    private suspend fun loadZipRepo(repoName: String) {
        when (val answer = repository.loadRepoZip(userLogin, repoName)) {
            is ResultWrapper.Success -> {
                saveFile(answer.value, repoName, downloadPath)
            }
            is ResultWrapper.Failure -> {
                val errMsg = handleError(answer.error)
                if (errMsg != null) {
                    _snackMessage.postValue(errMsg)
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
                _snackMessage.postValue(R.string.file_downlod_succes)
            }
            is ResultWrapper.Failure -> {
                _snackMessage.postValue(handleError(answer.error))
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

    fun retryBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            searchNew(userLogin)
        }
    }

    fun linkBtnClicked(item: GithubRepo) {
        _htmlUrl.postValue(item.url)
    }

    //Suppress ???? _additionalRepos.postValue(answer.value)
    //?? _snackMessage.postValue(errMsg) - ?????? ??????????.
    @SuppressLint("NullSafeMutableLiveData")
    fun onScrolledToEnd(lastVisibleItemPosition: Int, itemCount: Int) {
        if (lastVisibleItemPosition + VISIBLE_THRESHOLD > itemCount) {
            if (request?.isActive == true || allDownloaded) {
                return
            }
            resultsPage++
            request = viewModelScope.launch(Dispatchers.IO) {
                when (val answer = searchRepos(userLogin)) {
                    is ResultWrapper.Success -> {
                        if (answer.value.isEmpty()) {
                            _snackMessage.postValue(R.string.all_repos_download_text)
                            allDownloaded = true
                        } else {
                            _additionalRepos.postValue(answer.value)
                        }
                    }
                    is ResultWrapper.Failure -> {
                        val errMsg = handleError(answer.error)
                        if (errMsg != null) {
                            _snackMessage.postValue(errMsg)
                        }
                    }
                }
            }
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
