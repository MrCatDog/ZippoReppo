package com.example.zipporeppogithub.ui.search

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.network.GithubUserSearchResult
import com.example.zipporeppogithub.model.ErrorEntity
import com.example.zipporeppogithub.model.ErrorEntity.ApiError.*
import com.example.zipporeppogithub.model.ErrorEntity.DBError.*
import com.example.zipporeppogithub.utils.MutableLiveEvent
import com.example.zipporeppogithub.model.ResultWrapper
import com.example.zipporeppogithub.utils.USERS_RESULT_COUNT
import kotlinx.coroutines.*
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val repository: Repository
) : ViewModel() {

    companion object {
        const val GITHUB_API_DELAY: Long = 800
        const val VISIBLE_THRESHOLD = 5
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _users = MutableLiveData<List<GithubUserSearchResult.User>>()
    val users: LiveData<List<GithubUserSearchResult.User>>
        get() = _users

    private val _additionalUsers = MutableLiveData<List<GithubUserSearchResult.User>>()
    val additionalUsers: LiveData<List<GithubUserSearchResult.User>>
        get() = _additionalUsers

    private val _message = MutableLiveData<Int?>()
    val message: LiveData<Int?>
        get() = _message

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean>
        get() = _isError

    private val _snackMessage = MutableLiveEvent<Int>()
    val snackMessage: LiveData<Int>
        get() = _snackMessage

    private val _navigateToUserRepos = MutableLiveEvent<String>()
    val navigateToUserRepos: LiveData<String>
        get() = _navigateToUserRepos

    private var request: Job? = null
    private var resultsPage: Int = 1

    private suspend fun searchUsers(query: String): ResultWrapper<GithubUserSearchResult> =
        repository.loadUsersFromNetwork(query, USERS_RESULT_COUNT, resultsPage)

    private suspend fun searchNew(query: String) {
        _message.postValue(null)
        _isError.postValue(false)
        _isLoading.postValue(true)
        when (val answer = searchUsers(query)) {
            is ResultWrapper.Success -> {
                if (answer.value.resultsCount <= 0) {
                    _message.postValue(R.string.empty_result_text)
                    _users.postValue(emptyList())
                } else {
                    _users.postValue(answer.value.usersList)
                }
            }
            is ResultWrapper.Failure -> {
                val errMsg = handleError(answer.error)
                if (errMsg != null) {
                    _message.postValue(errMsg)
                    _isError.postValue(true)
                }
                _users.postValue(emptyList())
            }
        }
        _isLoading.postValue(false)
    }

    private fun handleError(error: ErrorEntity): Int? {
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
            is ErrorEntity.Cancel -> null
            else -> R.string.unknown_error_text
        }
    }

    fun listItemClicked(item: GithubUserSearchResult.User) {
        _navigateToUserRepos.postValue(item.username)
    }

    fun retryBtnClicked(query: String) {
        request = viewModelScope.launch(Dispatchers.IO) {
            searchNew(query)
        }
    }

    fun onSearchTextChanged(query: String) {
        if (query.isNotEmpty()) {
            if (request?.isActive == true) {
                request!!.cancel()
            }
            _users.postValue(emptyList())
            resultsPage = 1
            request = viewModelScope.launch(Dispatchers.IO) {
                //вот вздумалось бахнуть поиск "на лету",
                // а у гитахаба ограничение на 10 запросов в минуту для не аутентифицированных.
                // так шо лишний раз его не дёргаем, даём задержку
                //https://docs.github.com/en/rest/search?apiVersion=2022-11-28#rate-limit
                delay(GITHUB_API_DELAY)
                searchNew(query)
            }
        } else {
            _users.postValue(emptyList())
            _message.postValue(null)
            _isError.postValue(false)
        }
    }

    //Suppress на _snackMessage.postValue(errMsg) - баг линта.
    @SuppressLint("NullSafeMutableLiveData")
    fun onScrolledToEnd(lastVisibleItemPosition: Int, itemCount: Int, query: String) {
        if (lastVisibleItemPosition + VISIBLE_THRESHOLD > itemCount) {
            if (request?.isActive == true) {
                return
            }
            resultsPage++
            request = viewModelScope.launch(Dispatchers.IO) {
                when (val answer = searchUsers(query)) {
                    is ResultWrapper.Success -> {
                        if (answer.value.resultsCount <= 0) {
                            _snackMessage.postValue(R.string.empty_result_text)
                        } else {
                            _additionalUsers.postValue(answer.value.usersList)
                        }
                    }
                    is ResultWrapper.Failure -> {
                        val errMsg = handleError(answer.error)
                        if (errMsg != null) {
                            _snackMessage.postValue(errMsg) //когда они это исправят?
                        }
                    }
                }
            }
        }
    }
}
