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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SearchViewModel
@Inject constructor(
    private val repository: Repository
) : ViewModel() {

    companion object {
        const val GITHUB_API_DELAY: Long = 800
        const val VISIBLE_THRESHOLD = 5
    }

    private val reducer = MainReducer(UserSearchUiState.initial())
    val uiState: StateFlow<UserSearchUiState>
        get() = reducer.state

    private var request: Job? = null
    private var resultsPage: Int = 1

    private suspend fun searchUsers(query: String): ResultWrapper<GithubUserSearchResult> =
        repository.loadUsersFromNetwork(query, USERS_RESULT_COUNT, resultsPage)

    private suspend fun searchNew(
        query: String
    ) {
        reducer.sendEvent(SearchUiEvent.UsersLoading(query))
        when (val answer = searchUsers(query)) {
            is ResultWrapper.Success -> {
                if (answer.value.resultsCount <= 0) {
                    reducer.sendEvent(SearchUiEvent.NoUsersFound)
                } else {
                    reducer.sendEvent(SearchUiEvent.NewUsersFound(answer.value.usersList))
                }
            }
            is ResultWrapper.Failure -> {
                val errMsg = handleError(answer.error)
                if (errMsg != null) {
                    reducer.sendEvent(SearchUiEvent.SetError(errMsg))
                }
//                _users.postValue(emptyList()) todo: а надо ли?
            }
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

    fun screenNavigateOut() {
        reducer.sendEvent(SearchUiEvent.ScreenNavOut)
    }

    fun listItemClicked(user: GithubUserSearchResult.User) {
        reducer.sendEvent(SearchUiEvent.NavigateToUserRepos(user.username))
    }

    fun retryBtnClicked() {
        request = viewModelScope.launch(Dispatchers.IO) {
            val prevRequest = uiState.value.prevRequest
            if (prevRequest.isNotEmpty()) {
                searchNew(uiState.value.prevRequest) //todo
            } else {
                reducer.sendEvent(SearchUiEvent.SetError(R.string.unknown_error_text))
            }
        }
    }

    fun onSearchTextChanged(query: String) {
        reducer.sendEvent(SearchUiEvent.ClearUsers)
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
                searchNew(query)
            }
        }
    }

    fun onScrolled(lastVisibleItemPosition: Int, itemCount: Int) {
        if (lastVisibleItemPosition + VISIBLE_THRESHOLD > itemCount) {
            if (request?.isActive == true) {
                return
            }
            resultsPage++
            request = viewModelScope.launch(Dispatchers.IO) {
                searchNew(uiState.value.prevRequest)
            }
        }
    }

    private class MainReducer(initial: UserSearchUiState) {

        private val _state: MutableStateFlow<UserSearchUiState> = MutableStateFlow(initial)
        val state: StateFlow<UserSearchUiState>
            get() = _state

        fun setState(newState: UserSearchUiState) {
            _state.tryEmit(newState)
        }

        fun sendEvent(event: SearchUiEvent) {
            reduce(_state.value, event)
        }

        fun reduce(oldState: UserSearchUiState, event: SearchUiEvent) {
            when (event) {
                is SearchUiEvent.NavigateToUserRepos -> {
                    setState(oldState.copy(reposNav = event.userLogin))
                }
                is SearchUiEvent.ScreenNavOut -> {
                    setState(oldState.copy(reposNav = null))
                }
                is SearchUiEvent.ClearUsers -> {
                    setState(oldState.copy(users = emptyList(), errorMsg = null, prevRequest = ""))
                }
                is SearchUiEvent.UsersLoading -> {
                    setState(
                        oldState.copy(
                            isLoading = true,
                            errorMsg = null,
                            prevRequest = event.query
                        )
                    )
                }
                is SearchUiEvent.NewUsersFound -> {
                    val newUsersList = oldState.users.toMutableList()
                    newUsersList.addAll(event.users)
                    setState(
                        oldState.copy(
                            users = newUsersList,
                            errorMsg = null,
                            isLoading = false
                        )
                    )
                }
                is SearchUiEvent.NoUsersFound -> {
                    setState(oldState.copy(isLoading = false)) //todo
                }
                is SearchUiEvent.SetError -> {
                    setState(oldState.copy(errorMsg = event.errorMsgResource, isLoading = false))
                }
            }
        }
    }
}
