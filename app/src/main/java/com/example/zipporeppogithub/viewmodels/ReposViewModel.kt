package com.example.zipporeppogithub.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zipporeppogithub.model.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class ReposViewModel
@AssistedInject constructor(
    private val repository: Repository,
    @Assisted private val userLogin: String
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading
}

@AssistedFactory
interface ReposViewModelFactory {
    fun create(userLogin: String): ReposViewModel
}