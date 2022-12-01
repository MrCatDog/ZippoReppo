package com.example.zipporeppogithub.model.errorhandlers

import com.example.zipporeppogithub.utils.ErrorEntity
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection
import javax.inject.Inject

class NetworkErrorHandler @Inject constructor(): ErrorHandler {
    override fun handleError(error: Throwable): ErrorEntity {
        return when(error) {
            is IOException -> ErrorEntity.ApiError.Network
            is HttpException -> {
                when (error.code()) {
                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> ErrorEntity.ApiError.NotFound

                    // access denied
                    HttpURLConnection.HTTP_FORBIDDEN -> ErrorEntity.ApiError.AccessDenied

                    // unavailable service
                    HttpURLConnection.HTTP_UNAVAILABLE -> ErrorEntity.ApiError.ServiceUnavailable

                    // all the others will be treated as unknown error
                    else -> ErrorEntity.UnknownError
                }
            }
            is CancellationException -> ErrorEntity.Cancel
            else -> ErrorEntity.UnknownError
        }
    }
}