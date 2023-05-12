package com.example.zipporeppogithub.ui.history

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryReducer(initial: HistoryState) {

    private val _state: MutableStateFlow<HistoryState> = MutableStateFlow(initial)
    val state: StateFlow<HistoryState>
        get() = _state

    private fun setState(newState: HistoryState) {
        _state.tryEmit(newState)
    }

    fun sendEvent(event: HistoryEvent) {
        reduce(_state.value, event)
    }

    private fun reduce(oldState: HistoryState, event: HistoryEvent) {
        when (event) {
            is HistoryEvent.ReposLoading -> {
                setState(
                    oldState.copy(
                        isLoading = true,
                        errorMsg = null
                    )
                )
            }
            is HistoryEvent.SetError -> {
                setState(oldState.copy(errorMsg = event.errorMsgResource, isLoading = false))
            }
            is HistoryEvent.RecordsFound -> {
                setState(
                    oldState.copy(
                        historyRecords = event.repos,
                        errorMsg = null,
                        isLoading = false
                    )
                )
            }
            is HistoryEvent.RecordsEmpty -> {
                setState(
                    oldState.copy(
                        isEmpty = true, isLoading = false
                    )
                )
            }
        }
    }
}