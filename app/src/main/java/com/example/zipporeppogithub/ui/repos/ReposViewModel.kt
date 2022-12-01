package com.example.zipporeppogithub.ui.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.network.GithubRepo
import com.example.zipporeppogithub.utils.ErrorEntity
import com.example.zipporeppogithub.utils.MutableLiveEvent
import com.example.zipporeppogithub.utils.ResultWrapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.InputStream


class ReposViewModel
@AssistedInject constructor(
    private val repository: Repository,
    @Assisted("user") private val userLogin: String,
    @Assisted("path") private val downloadPath: String
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _repos = MutableLiveData<List<GithubRepo>>()
    val repos: LiveData<List<GithubRepo>>
        get() = _repos

    private val _message = MutableLiveEvent<Int?>()
    val message: LiveData<Int?>
        get() = _message

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean>
        get() = _isError

    private val _htmlUrl = MutableLiveEvent<String>()
    val url: LiveData<String>
        get() = _htmlUrl

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            requestRepos(userLogin)
            _isLoading.postValue(false)
        }
    }

    private suspend fun requestRepos(username: String) {
        _isError.postValue(false)
        _message.postValue(null)
        _repos.postValue(emptyList())
        _isLoading.postValue(true)

        when (val answer = repository.loadUserRepos(username)) {
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
                _message.postValue(handleError(answer.error))
                _isError.postValue(true)
            }
        }
        _isLoading.postValue(false)
    }

    fun downloadBtnClicked(item: GithubRepo) {
        viewModelScope.launch(Dispatchers.IO) { loadZipRepo(item.name) }
    }

    fun linkBtnClicked(item: GithubRepo) {
        _htmlUrl.postValue(item.url)
    }

    private suspend fun loadZipRepo(repoName: String) {
        _isError.postValue(false)
        _message.postValue(null)

        when (val answer = repository.loadRepoZip(userLogin, repoName)) {
            is ResultWrapper.Success -> {
                saveFile(answer.value, repoName, downloadPath)
            }
            is ResultWrapper.Failure -> {
                _message.postValue(handleError(answer.error))
                _isError.postValue(true)
            }
        }
    }

    private fun saveFile(body: ResponseBody, repoName: String, path: String) {
//        val workManager = WorkManager.getInstance(Context)
//        workManager.enqueue(OneTimeWorkRequest.Builder(FooWorker::class).build())
//todo

        var input: InputStream? = null
        try {
            input = body.byteStream()
            val fos = FileOutputStream(path.plus("/").plus(repoName))
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        } catch (e: Exception) {
            _message.postValue(R.string.file_download_error)
        } finally {
            input?.close()
        }
    }

    private fun handleError(error: ErrorEntity): Int {
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
            is ErrorEntity.UnknownError -> R.string.unknown_error_text
        }
    }

    fun retryBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) {  }
    }
}

@AssistedFactory
interface ReposViewModelFactory {
    fun create(
        @Assisted("user") userLogin: String,
        @Assisted("path") downloadPath: String
    ): ReposViewModel
}