package com.example.zipporeppogithub.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.Repository
import com.example.zipporeppogithub.model.db.HistoryRecord
import com.example.zipporeppogithub.model.network.GithubRepo
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

    private val _error = MutableLiveEvent<Int>()
    val error: LiveData<Int>
        get() = _error

    private val _isEmpty = MutableLiveData(false)
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            requestHistory()
            _isLoading.postValue(false)
        }
    }

    private suspend fun requestHistory() {
        when (val answer = repository.getDownloadHistory()) {
            is ResultWrapper.Success -> {
                val repos = answer.value
                if (repos.isNotEmpty()) {
                    _historyRecords.postValue(repos)
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
        return if (error is ErrorEntity.DBError) {
            when (error) {
                ErrorEntity.DBError.NoPermission -> R.string.no_permission_error_text
                ErrorEntity.DBError.Common -> R.string.common_db_error_text
            }
        } else {
            R.string.unknown_error_text
        }
    }


}