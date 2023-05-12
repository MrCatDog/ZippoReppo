package com.example.zipporeppogithub.ui.repos.composepreview

import com.example.zipporeppogithub.model.network.GithubRepo

data class RepoItemData(
    val repo: GithubRepo,
    val downloadBtnCallback: (GithubRepo) -> Unit,
    val linkBtnCallback: (GithubRepo) -> Unit
)
