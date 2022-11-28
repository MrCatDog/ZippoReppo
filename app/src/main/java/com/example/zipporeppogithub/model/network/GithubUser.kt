package com.example.zipporeppogithub.model.network

import com.google.gson.annotations.SerializedName

data class GithubUser(
    @SerializedName("login")
    val username: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)