package com.example.zipporeppogithub.ui.repos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.network.GithubRepo

@Composable
fun ReposScreen(
    viewModel: ReposViewModel
) {
    val repos = viewModel.reposToShow.observeAsState().value
    val scrollState = rememberScrollState()
    val isEndReached by remember {
        derivedStateOf {
            scrollState.value >= scrollState.maxValue - 5
        }
    }

    if (isEndReached) {
        LaunchedEffect(Unit) {
            //todo react on scroll end
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        if (repos != null) {
            items(repos) {
                RepoItem(repo = it, viewModel::downloadBtnClicked, viewModel::linkBtnClicked)
            }
        }
    }
}

@Composable
fun RepoItem(
    repo: GithubRepo,
    downloadBtnCallback: (GithubRepo) -> Unit,
    linkBtnCallback: (GithubRepo) -> Unit
) {
    Row() {
        Column() {
            Text(
                text = repo.name
            )
            Button(onClick = { linkBtnCallback(repo) }) {
                Text(
                    text = stringResource(id = R.string.repo_open_in_browser_btn_text)
                )
            }
        }
        Button(onClick = { downloadBtnCallback(repo) }) {
            Icon(
                painter = painterResource(id = R.drawable.repo_download_img),
                contentDescription = stringResource(
                    id = R.string.repo_download_content_desc
                )
            )
        }
    }
}