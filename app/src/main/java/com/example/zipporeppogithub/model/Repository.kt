package com.example.zipporeppogithub.model

import com.example.zipporeppogithub.model.db.HistoryDao
import com.example.zipporeppogithub.model.db.HistoryRecord
import com.example.zipporeppogithub.model.errorhandlers.DBErrorHandler
import com.example.zipporeppogithub.model.errorhandlers.ErrorHandler
import com.example.zipporeppogithub.model.errorhandlers.ExternalStorageErrorHandler
import com.example.zipporeppogithub.model.errorhandlers.NetworkErrorHandler
import com.example.zipporeppogithub.model.network.GithubApi
import com.example.zipporeppogithub.model.network.GithubRepo
import com.example.zipporeppogithub.model.network.GithubUserSearchResult
import com.example.zipporeppogithub.ui.repos.ReposViewModel
import com.example.zipporeppogithub.utils.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

class Repository @Inject constructor(
    private val githubApi: GithubApi,
    private val historyDao: HistoryDao,
    private val networkErrorHandler: NetworkErrorHandler,
    private val dbErrorHandler: DBErrorHandler,
    private val externalStorageErrorHandler: ExternalStorageErrorHandler
) {
    suspend fun loadUsersFromNetwork(
        query: String,
        resultsPerPage: Int,
        pageNumber: Int
    ): ResultWrapper<GithubUserSearchResult> {
        return safeCall(
            Dispatchers.IO,
            networkErrorHandler
        ) { githubApi.getUserList(query, resultsPerPage, pageNumber) }
    }

    suspend fun loadUserRepos(
        username: String,
        resultsPerPage: Int,
        pageNumber: Int
    ): ResultWrapper<List<GithubRepo>> {
        return safeCall(Dispatchers.IO, networkErrorHandler) {
            githubApi.getUserRepos(
                username,
                resultsPerPage,
                pageNumber
            )
        }
    }

    suspend fun loadRepoZip(username: String, repoName: String): ResultWrapper<ResponseBody> {
        return safeCall(Dispatchers.IO, networkErrorHandler) {
            githubApi.getRepoZip(
                username,
                repoName
            )
        }
    }

    suspend fun saveDownloadInHistory(historyRecord: HistoryRecord): ResultWrapper<Unit> {
        return safeCall(Dispatchers.IO, dbErrorHandler) { historyDao.insert(historyRecord) }
    }

    suspend fun getDownloadHistory(): ResultWrapper<List<HistoryRecord>> {
        return safeCall(Dispatchers.IO, dbErrorHandler) {
            historyDao.getAllRecords()
        }
    }

    suspend fun saveFileInExternalStorage(file: ResponseBody, path: String) : ResultWrapper<Unit> {
        return safeCall(Dispatchers.IO, externalStorageErrorHandler) {
            saveFile(file, path)
        }
    }

    private fun saveFile(file: ResponseBody, path: String) {
        var input: InputStream? = null
        try {
            input = file.byteStream()
            val fos = FileOutputStream(path)
            fos.use { output ->
                val buffer = ByteArray(ReposViewModel.BUFF_SIZE) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        } finally {
            input?.close()
        }
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
