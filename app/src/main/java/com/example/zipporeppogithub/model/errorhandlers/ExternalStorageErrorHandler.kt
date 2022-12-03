package com.example.zipporeppogithub.model.errorhandlers

import com.example.zipporeppogithub.utils.ErrorEntity
import kotlinx.coroutines.CancellationException
import java.io.FileNotFoundException
import javax.inject.Inject

class ExternalStorageErrorHandler @Inject constructor() : ErrorHandler {
    override fun handleError(error: Throwable): ErrorEntity {
        return when(error) {
            is FileNotFoundException -> ErrorEntity.ExtError.Permission
            is CancellationException -> ErrorEntity.Cancel
            else -> ErrorEntity.ExtError.Common
        }
    }
}
