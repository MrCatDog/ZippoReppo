package com.example.zipporeppogithub.ui.search

import com.example.zipporeppogithub.model.network.GithubUserSearchResult

data class UserSearchUiState(
    val reposNav: String?,
    val isLoading: Boolean,
    val users: List<GithubUserSearchResult.User>,//отталкиваться с Additional от пустоты списка
    val errorMsg: Int?,
    val prevRequest: String
) {
    companion object {
        fun initial() = UserSearchUiState(
            isLoading = false,
            prevRequest = "",
            users = emptyList(),
            errorMsg = null,
            reposNav = null
        )
    }
}
