package com.example.zipporeppogithub.model.network

import com.google.gson.annotations.SerializedName

data class GithubUserSearchResult(
    @SerializedName("total_count")
    val resultsCount: Int,
    @SerializedName("items")
    val usersList: List<User>
) {
    data class User(
        @SerializedName("login")
        val username: String,
        @SerializedName("avatar_url")
        val avatarUrl: String
    )
}