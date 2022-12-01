package com.example.zipporeppogithub.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.db.HistoryRecord
import com.example.zipporeppogithub.utils.ErrorEntity
import com.example.zipporeppogithub.utils.MutableLiveEvent
import com.example.zipporeppogithub.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryViewModel
@Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _historyRecords = MutableLiveData<List<HistoryRecord>>()
    val historyRecords: LiveData<List<HistoryRecord>>
        get() = _historyRecords

    private val _message = MutableLiveEvent<Int?>()
    val message: LiveData<Int?>
        get() = _message

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean>
        get() = _isError

    init {
        viewModelScope.launch(Dispatchers.IO) { requestHistory() }
    }

    private suspend fun requestHistory() {
        _isError.postValue(false)
        _message.postValue(null)
        _historyRecords.postValue(emptyList())
        _isLoading.postValue(true)

        when (val answer = repository.getDownloadHistory()) {
            is ResultWrapper.Success -> {
                val repos = answer.value
                if (repos.isNotEmpty()) {
                    _historyRecords.postValue(repos)
                } else {
                    _message.postValue(R.string.empty_history_text)
                    _historyRecords.postValue(emptyList())
                }
            }
            is ResultWrapper.Failure -> {
                _message.postValue(handleError(answer.error))
                _isError.postValue(true)
            }
        }
        _isLoading.postValue(false)
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

    fun retryBtnClicked() {
        viewModelScope.launch(Dispatchers.IO) { requestHistory() }
    }


}