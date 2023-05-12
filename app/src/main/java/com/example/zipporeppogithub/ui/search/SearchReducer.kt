package com.example.zipporeppogithub.ui.search

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//todo Вынос всех общих черт Reducer-ов в отдельный класс(Abstract?)
class SearchReducer(initial: SearchState) {

    private val _state: MutableStateFlow<SearchState> = MutableStateFlow(initial)
    val state: StateFlow<SearchState>
        get() = _state

    private fun setState(newState: SearchState) {
        _state.tryEmit(newState)
    }

    fun sendEvent(event: SearchEvent) {
        reduce(_state.value, event)
    }

    private fun reduce(oldState: SearchState, event: SearchEvent) {
        when (event) {
            is SearchEvent.NavigateToUserRepos -> {
                setState(oldState.copy(reposNav = event.userLogin))
            }
            is SearchEvent.ScreenNavOut -> {
                setState(oldState.copy(reposNav = null))
            }
            is SearchEvent.ClearUsers -> {
                setState(oldState.copy(users = emptyList(), errorMsg = null, prevRequest = ""))
            }
            is SearchEvent.UsersLoading -> {
                setState(
                    oldState.copy(
                        isLoading = true,
                        errorMsg = null,
                        prevRequest = event.query
                    )
                )
            }
            is SearchEvent.NewUsersFound -> {
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
            is SearchEvent.NoUsersFound -> {
                setState(oldState.copy(isLoading = false, )) //todo
            }
            is SearchEvent.SetError -> {
                setState(oldState.copy(errorMsg = event.errorMsgResource, isLoading = false))
            }
        }
    }
}