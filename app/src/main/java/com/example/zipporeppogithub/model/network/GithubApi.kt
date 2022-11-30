package com.example.zipporeppogithub.model.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface GithubApi {

    @GET("/search/users")
    suspend fun getUserList(
        @Query("q") loginQuery: String
    ): GithubUserSearchResult

    @GET("/users/{userLogin}/repos")
    suspend fun getUserRepos(
        @Path("userLogin") userLogin: String
    ) : List<GithubRepo>

    @Streaming
    @GET("/repos/{owner}/{repo}/zipball")
    suspend fun getRepoZip(
        @Path("owner") userLogin: String,
        @Path("repo") repoName: String
    ) : ResponseBody
}