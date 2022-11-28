package com.example.zipporeppogithub.model.network

import com.google.gson.annotations.SerializedName

data class GithubRepo(
    @SerializedName("name")
    val name: String,
    @SerializedName("html_url")
    val url: String
)