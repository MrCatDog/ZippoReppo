package com.example.zipporeppogithub.ui.repos

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReposReducer(initial: ReposState) {

    private val _state: MutableStateFlow<ReposState> = MutableStateFlow(initial)
    val state: StateFlow<ReposState>
        get() = _state

    private fun setState(newState: ReposState) {
        _state.tryEmit(newState)
    }

    fun sendEvent(event: ReposEvent) {
        reduce(_state.value, event)
    }

    private fun reduce(oldState: ReposState, event: ReposEvent) {
        when (event) {
            is ReposEvent.NavigateToHtml -> {
                setState(oldState.copy(htmlLink = event.html))
            }
            is ReposEvent.ScreenNavigateOut -> {
                setState(oldState.copy(htmlLink = null))
            }
            is ReposEvent.PermissionRequested -> {
                setState(
                    oldState.copy(
                        isPermissionRequired = event.isRequested
                    )
                )
            }
            is ReposEvent.ReposLoading -> {
                setState(
                    oldState.copy(
                        isLoading = true,
                        errorMsg = null
                    )
                )
            }
            is ReposEvent.NewReposFound -> {
                val newReposList = oldState.repos.toMutableList()
                newReposList.addAll(event.repos)
                setState(
                    oldState.copy(
                        repos = newReposList,
                        errorMsg = null,
                        isLoading = false
                    )
                )
            }
            is ReposEvent.NoReposFound -> {
                //todo
            }
            is ReposEvent.SetError -> {
                setState(oldState.copy(errorMsg = event.errorMsgResource, isLoading = false))
            }
            is ReposEvent.ShowSnack -> {
                //todo
            }
        }
    }
}