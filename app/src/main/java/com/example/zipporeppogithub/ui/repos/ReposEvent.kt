package com.example.zipporeppogithub.ui.repos

import com.example.zipporeppogithub.model.network.GithubRepo

sealed class ReposEvent {
    object ReposLoading : ReposEvent()
    data class SetError(val errorMsgResource: Int) : ReposEvent()
    data class NewReposFound(val repos: List<GithubRepo>) : ReposEvent()
    object NoReposFound : ReposEvent()

    data class PermissionRequested(val isRequested: Boolean) : ReposEvent()

    data class NavigateToHtml(val html: String) : ReposEvent()
    object ScreenNavigateOut : ReposEvent()

    data class ShowSnack(val msgResId: Int) : ReposEvent()
}