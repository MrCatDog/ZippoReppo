package com.example.zipporeppogithub.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zipporeppogithub.model.Repository
import javax.inject.Inject

class HistoryViewModel
    @Inject constructor(
        private val repository: Repository,
    ): ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
}