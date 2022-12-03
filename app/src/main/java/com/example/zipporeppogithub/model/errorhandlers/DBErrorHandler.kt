package com.example.zipporeppogithub.model.errorhandlers

import android.database.sqlite.SQLiteAccessPermException
import android.database.sqlite.SQLiteException
import com.example.zipporeppogithub.model.ErrorEntity
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class DBErrorHandler @Inject constructor() : ErrorHandler {
    override fun handleError(error: Throwable): ErrorEntity {
        return when(error) {
            is SQLiteAccessPermException -> ErrorEntity.DBError.NoPermission
            is SQLiteException -> ErrorEntity.DBError.Common
            is CancellationException -> ErrorEntity.Cancel
            else -> ErrorEntity.UnknownError
        }
    }
}