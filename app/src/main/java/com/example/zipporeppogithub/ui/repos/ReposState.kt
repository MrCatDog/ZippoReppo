package com.example.zipporeppogithub.ui.repos

import com.example.zipporeppogithub.model.network.GithubRepo

data class ReposState(
    val isLoading: Boolean,
    val repos: List<GithubRepo>,
    val errorMsg: Int?,
    val htmlLink: String?,
    val isPermissionRequired: Boolean,
    val isLastAnswerWasEmpty: Boolean
) {
    companion object {
        fun initial() = ReposState(
            isLoading = false,
            repos = emptyList(),
            errorMsg = null,
            htmlLink = null,
            isPermissionRequired = false,
            isLastAnswerWasEmpty = false
        )
    }
}
