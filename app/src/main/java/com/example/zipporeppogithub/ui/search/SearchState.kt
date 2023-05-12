package com.example.zipporeppogithub.ui.search

import com.example.zipporeppogithub.model.network.GithubUserSearchResult

data class SearchState(
    val reposNav: String?,
    val isLoading: Boolean,
    val users: List<GithubUserSearchResult.User>,
    val errorMsg: Int?,
    val prevRequest: String,
    val isLastAnswerEmpty : Boolean
) {
    companion object {
        fun initial() = SearchState(
            isLoading = false,
            prevRequest = "",
            users = emptyList(),
            errorMsg = null,
            reposNav = null,
            isLastAnswerEmpty = false
        )
    }
}
