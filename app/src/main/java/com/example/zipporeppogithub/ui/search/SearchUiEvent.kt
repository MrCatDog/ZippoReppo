package com.example.zipporeppogithub.ui.search

import com.example.zipporeppogithub.model.network.GithubUserSearchResult

sealed class SearchUiEvent {
    data class NavigateToUserRepos(val userLogin: String) : SearchUiEvent()
    object ScreenNavOut : SearchUiEvent()

    object ClearUsers : SearchUiEvent()

    data class UsersLoading(val query: String) : SearchUiEvent()
    data class SetError(val errorMsgResource: Int) : SearchUiEvent()
    data class NewUsersFound(val users: List<GithubUserSearchResult.User>) : SearchUiEvent()
    object NoUsersFound : SearchUiEvent()

}