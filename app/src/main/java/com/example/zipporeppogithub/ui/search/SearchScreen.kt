package com.example.zipporeppogithub.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.network.GithubUserSearchResult

@Composable
fun SearchScreen(
    userSearchViewModel: SearchViewModel,
    onUserReposNavigation: (userLogin: String) -> Unit
) {
    val state by userSearchViewModel.uiState.collectAsState()

    val reposNav = state.reposNav
    if (reposNav != null) {
        onUserReposNavigation(reposNav)
        userSearchViewModel.screenNavigateOut()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchField(
            prevQuery = state.prevRequest,
            onQueryChange = userSearchViewModel::onSearchTextChanged
        )

        if (state.users.isNotEmpty()) {
            SearchedUsersList(
                users = state.users,
                onUserClick = userSearchViewModel::listItemClicked,
                onScroll = userSearchViewModel::onScrolled
            )
        }

        if (state.isLoading) {
            CircularLoadingIndicator()
        }

        val errMsgResId = state.errorMsg
        if (errMsgResId != null) {
            ErrorView(
                errMsgResId = errMsgResId,
                onRetryBtnClick = userSearchViewModel::retryBtnClicked
            )
        }
    }
}

@Composable
fun SearchField(prevQuery: String, onQueryChange: (String) -> Unit) { //todo prev
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        searchQuery = prevQuery
    }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = searchQuery,
        onValueChange = {
            searchQuery = it
            onQueryChange(it)
        },
        label = { Text(stringResource(id = R.string.search_input_invite)) }
    )
}

@Composable
fun SearchedUsersList(
    users: List<GithubUserSearchResult.User>,
    onUserClick: (GithubUserSearchResult.User) -> Unit,
    onScroll: (Int, Int) -> Unit
) {
    val nestedScroll = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                onScroll(consumed.y.toInt(), available.y.toInt())
                return Offset.Zero
            }
        }
    }

    LazyColumn(
        modifier = Modifier.nestedScroll(nestedScroll)
    ) {
        items(users) {
            SearchResultItem(user = it, onUserClick)
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

@Composable
fun ErrorView(errMsgResId: Int, onRetryBtnClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(id = errMsgResId))
            Button(onClick = onRetryBtnClick) {
                Text(text = stringResource(id = R.string.retry_btn_text))
            }
        }
    }
}

@Composable
fun CircularLoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}