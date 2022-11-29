package com.example.zipporeppogithub.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.network.GithubRepo
import com.example.zipporeppogithub.model.network.GithubUserSearchResult
import com.example.zipporeppogithub.utils.ErrorEntity
import com.example.zipporeppogithub.utils.MutableLiveEvent
import com.example.zipporeppogithub.utils.ResultWrapper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class ReposViewModel
@AssistedInject constructor(
    private val repository: Repository,
    @Assisted private val userLogin: String
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _repos = MutableLiveData<List<GithubRepo>>()
    val repos: LiveData<List<GithubRepo>>
        get() = _repos

    private val _error = MutableLiveEvent<Int>()
    val error: LiveData<Int>
        get() = _error

    private val _isEmpty = MutableLiveData<Boolean>(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    private suspend fun requestRepos(username : String) {
        when (val answer = repository.loadUserRepos(username)) {
            is ResultWrapper.Success -> {
                val repos = answer.value
                if(repos.isNotEmpty()) {
                    _repos.postValue(repos)
                } else {
                    _isEmpty.postValue(true)
                }
            }
            is ResultWrapper.Failure -> {
                _error.postValue(handleError(answer.error))
            }
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

    fun listItemClicked(item: GithubRepo) {

    }
}

@AssistedFactory
interface ReposViewModelFactory {
    fun create(userLogin: String): ReposViewModel
}