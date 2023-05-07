package com.example.zipporeppogithub.ui.search

import com.example.zipporeppogithub.model.network.GithubUserSearchResult

sealed class UserSearchUiState {
    object UsersSearchNoItemsState : UserSearchUiState()
    object UsersSearchLoadingState : UserSearchUiState()
    class UsersSearchLoadedState(val users: List<GithubUserSearchResult.User>) : UserSearchUiState()
    class UsersSearchErrorState(val errorMsg : String) : UserSearchUiState()
    object UsersSearchNothingFoundState : UserSearchUiState()
}
