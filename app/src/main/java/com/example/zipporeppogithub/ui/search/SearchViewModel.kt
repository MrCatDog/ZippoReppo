package com.example.zipporeppogithub.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.network.GithubUserSearchResult
import com.example.zipporeppogithub.model.ErrorEntity
import com.example.zipporeppogithub.model.ErrorEntity.ApiError.*
import com.example.zipporeppogithub.model.ErrorEntity.DBError.*
import com.example.zipporeppogithub.model.ResultWrapper
import com.example.zipporeppogithub.utils.USERS_RESULT_COUNT
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val repository: Repository
) : ViewModel() {

    companion object {
        const val GITHUB_API_DELAY: Long = 800
    }

    private val reducer = SearchReducer(SearchState.initial())
    val uiState: StateFlow<SearchState>
        get() = reducer.state

    private var request: Job? = null
    private var resultsPage: Int = 1

    private suspend fun searchUsers(query: String): ResultWrapper<GithubUserSearchResult> =
        repository.loadUsersFromNetwork(query, USERS_RESULT_COUNT, resultsPage)

    private suspend fun getUsers(
        query: String
    ) {
        reducer.sendEvent(SearchEvent.UsersLoading(query))
        when (val answer = searchUsers(query)) {
            is ResultWrapper.Success -> {
                if (answer.value.resultsCount <= 0) {
                    reducer.sendEvent(SearchEvent.NoUsersFound)
                } else {
                    reducer.sendEvent(SearchEvent.NewUsersFound(answer.value.usersList))
                }
            }
            is ResultWrapper.Failure -> {
                val errMsg = handleError(answer.error)
                if (errMsg != null) {
                    reducer.sendEvent(SearchEvent.SetError(errMsg))
                }
            }
        }
    }

    fun screenNavigateOut() {
        reducer.sendEvent(SearchEvent.ScreenNavOut)
    }

    fun listItemClicked(user: GithubUserSearchResult.User) {
        reducer.sendEvent(SearchEvent.NavigateToUserRepos(user.username))
    }

    fun retryBtnClicked() {
        request = viewModelScope.launch(Dispatchers.IO) {
            val prevRequest = uiState.value.prevRequest
            if (prevRequest.isNotEmpty()) {
                getUsers(uiState.value.prevRequest)
            } else {
                reducer.sendEvent(SearchEvent.ClearUsers)
            }
        }
    }

    fun onSearchTextChanged(query: String) {
        reducer.sendEvent(SearchEvent.ClearUsers)
        if (request?.isActive == true) {
            request!!.cancel()
        }
        if (query.isNotEmpty()) {
            resultsPage = 1
            request = viewModelScope.launch(Dispatchers.IO) {
                //вот вздумалось бахнуть поиск "на лету",
                // а у гитахаба ограничение на 10 запросов в минуту для не аутентифицированных.
                // так шо лишний раз его не дёргаем, даём задержку
                //https://docs.github.com/en/rest/search?apiVersion=2022-11-28#rate-limit
                delay(GITHUB_API_DELAY)
                getUsers(query)
            }
        }
    }

    fun onScrolled() {
        if (request?.isActive == true) {
            return
        }
        resultsPage++
        request = viewModelScope.launch(Dispatchers.IO) {
            getUsers(uiState.value.prevRequest)
        }
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
}
