package com.example.zipporeppogithub.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.ErrorEntity
import com.example.zipporeppogithub.model.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryViewModel
@Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val reducer = HistoryReducer(HistoryState.initial())
    val uiState: StateFlow<HistoryState>
        get() = reducer.state

    init {
        getHistory()
    }

    private suspend fun requestHistory() {
        reducer.sendEvent(HistoryEvent.ReposLoading)
        when (val answer = repository.getDownloadHistory()) {
            is ResultWrapper.Success -> {
                val repos = answer.value
                if(repos.isNotEmpty()) {
                    reducer.sendEvent(HistoryEvent.RecordsFound(repos))
                } else {
                    reducer.sendEvent(HistoryEvent.RecordsEmpty)
                }
            }
            is ResultWrapper.Failure -> {
                reducer.sendEvent(HistoryEvent.SetError(handleError(answer.error)))
            }
        }
    }

    private fun handleError(error: ErrorEntity): Int {
        return if (error is ErrorEntity.DBError) {
            when (error) {
                ErrorEntity.DBError.NoPermission -> R.string.no_permission_error_text
                ErrorEntity.DBError.Common -> R.string.common_db_error_text
            }
        } else {
            R.string.unknown_error_text
        }
    }

    fun getHistory() {
        viewModelScope.launch(Dispatchers.IO) { requestHistory() }
    }

}
