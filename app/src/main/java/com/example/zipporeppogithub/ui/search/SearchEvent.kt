package com.example.zipporeppogithub.ui.search

import com.example.zipporeppogithub.model.network.GithubUserSearchResult

sealed class SearchEvent {
    data class NavigateToUserRepos(val userLogin: String) : SearchEvent()
    object ScreenNavOut : SearchEvent()

    object ClearUsers : SearchEvent()

    data class UsersLoading(val query: String) : SearchEvent()
    data class SetError(val errorMsgResource: Int) : SearchEvent()
    data class NewUsersFound(val users: List<GithubUserSearchResult.User>) : SearchEvent()
    object NoUsersFound : SearchEvent()

}