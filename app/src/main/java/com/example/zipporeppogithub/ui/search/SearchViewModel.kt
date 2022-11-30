package com.example.zipporeppogithub.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.network.GithubUserSearchResult
import com.example.zipporeppogithub.utils.ErrorEntity
import com.example.zipporeppogithub.utils.ErrorEntity.ApiError.*
import com.example.zipporeppogithub.utils.ErrorEntity.DBError.*
import com.example.zipporeppogithub.utils.ErrorEntity.UnknownError
import com.example.zipporeppogithub.utils.MutableLiveEvent
import com.example.zipporeppogithub.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _users = MutableLiveData<List<GithubUserSearchResult.User>>()
    val users: LiveData<List<GithubUserSearchResult.User>>
        get() = _users

    private val _error = MutableLiveData<Int?>()
    val error: LiveData<Int?>
        get() = _error

    private val _isAnswerEmpty = MutableLiveData(false)
    val isAnswerEmpty: LiveData<Boolean>
        get() = _isAnswerEmpty

    private val _navigateToUserRepos = MutableLiveEvent<String>()
    val navigateToUserRepos: LiveData<String>
        get() = _navigateToUserRepos

    private var request: Job? = null

    private suspend fun searchUsers(query: String) {
        _users.postValue(emptyList())
        _error.postValue(null)
        _isAnswerEmpty.postValue(false)
        _isLoading.postValue(true)

        when (val answer = repository.loadUsersFromNetwork(query)) {
            is ResultWrapper.Success -> {
                if (answer.value.resultsCount <= 0) {
                    _isAnswerEmpty.postValue(true)
                } else {
                    _users.postValue(answer.value.usersList)
                }
            }
            is ResultWrapper.Failure -> {
                _error.postValue(handleError(answer.error))
            }
        }
        _isLoading.postValue(false)
    }

    private fun handleError(error: ErrorEntity): Int {
        return when (error) {
            is ErrorEntity.ApiError -> when (error) {
                Network -> R.string.network_error_text
                NotFound -> R.string.not_found_error_text
                AccessDenied -> R.string.access_denied_error_text
                ServiceUnavailable -> R.string.service_unavailable_error_text
            }
            is ErrorEntity.DBError -> when (error) {
                NoPermission -> R.string.no_permission_error_text
                Common -> R.string.common_db_error_text
            }
            is UnknownError -> R.string.unknown_error_text
        }
    }

    fun listItemClicked(item: GithubUserSearchResult.User) {
        _navigateToUserRepos.postValue(item.username)
    }

    fun retryBtnClicked(query: String) {
        viewModelScope.launch(Dispatchers.IO) { searchUsers(query) }
    }

    fun onSearchTextChanged(query: String) {
        if (query.isNotEmpty()) {
            if (request?.isActive == true) {
                request!!.cancel() //todo
            }
            request = viewModelScope.launch(Dispatchers.IO) { searchUsers(query) }
        } else {
            _users.postValue(emptyList())
            _error.postValue(null)
            _isAnswerEmpty.postValue(false)
            _isLoading.postValue(true)
        }

    }
}