package com.example.zipporeppogithub.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.network.GithubUserSearchResult

@Composable
fun SearchScreen(
    userSearchViewModel: SearchViewModel,
    onUserClick: (item: GithubUserSearchResult.User) -> Unit
) {

//    val userSearchUiState = userSearchViewModel.uiState.collectAsState().value
//    when (userSearchUiState) {
//        is UserSearchUiState.UsersSearchNoItemsState -> {}
//        is UserSearchUiState.UsersSearchLoadingState -> {}
//        is UserSearchUiState.UsersSearchLoadedState -> {}
//        is UserSearchUiState.UsersSearchErrorState -> {}
//        is UserSearchUiState.UsersSearchNothingFoundState -> {}
//    }


    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
//    val scrollStateLazy = rememberLazyListState()
    val isEndReached by remember {
        derivedStateOf {
            scrollState.value >= scrollState.maxValue - 5
        }
    }

    if (isEndReached) {
        LaunchedEffect(Unit) {
            userSearchViewModel.onScrolledToEnd(
                scrollState.value,
                scrollState.maxValue,
                searchQuery
            ) //todo это нужно ещё отработать, когда запрос выполнится
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                userSearchViewModel.onSearchTextChanged(it)
            },
            label = { Text(stringResource(id = R.string.search_input_invite)) }
        )

        val users = userSearchViewModel.users.observeAsState().value

        LazyColumn {
            if (users != null) {
                items(users) {
                    SearchResultItem(user = it, onUserClick)
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
    user: GithubUserSearchResult.User,
    onClickListener: (GithubUserSearchResult.User) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClickListener(user) }) {
        Text(
            text = user.username,
            fontSize = dimensionResource(id = R.dimen.search_item_username_size).value.sp,
            modifier = Modifier.padding(dimensionResource(id = R.dimen.search_item_padding))
        )
        Divider(
            modifier = Modifier.padding(
                horizontal = dimensionResource(
                    id = R.dimen.search_item_div_padding_horizontal
                )
            ),
            thickness = dimensionResource(id = R.dimen.search_item_div_height),
            color = colorResource(id = R.color.purple_200)
        )
    }
}