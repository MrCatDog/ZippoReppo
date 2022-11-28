package com.example.zipporeppogithub.model

import com.example.zipporeppogithub.model.errorhandlers.ErrorHandler
import com.example.zipporeppogithub.model.errorhandlers.NetworkErrorHandler
import com.example.zipporeppogithub.model.network.GithubApi
import com.example.zipporeppogithub.model.network.GithubUser
import com.example.zipporeppogithub.utils.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val githubApi: GithubApi,
    private val networkErrorHandler: NetworkErrorHandler
) {
    suspend fun loadUsersFromNetwork(query: String) : ResultWrapper<List<GithubUser>> {
        return safeCall(Dispatchers.IO, networkErrorHandler) { githubApi.getUserList(query) } //todo transfer to domain model
    }

    private suspend fun <T> safeCall(
        dispatcher: CoroutineDispatcher,
        errorHandler: ErrorHandler,
        call: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(call.invoke())
            } catch (throwable: Throwable) {
                ResultWrapper.Failure(errorHandler.handleError(throwable))
            }
        }
    }
}