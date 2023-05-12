package com.example.zipporeppogithub.ui.repos.composepreview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.zipporeppogithub.model.network.GithubRepo

//колхозить вот это каждый раз чтобы просто посмотреть как это будет выглядеть...
class RepoItemDataProvider : PreviewParameterProvider<RepoItemData> {
    override val values = sequenceOf(
        RepoItemData(
            repo = GithubRepo(
                "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFf",
                "url"
            ), downloadBtnCallback = {}, linkBtnCallback = {}),
        RepoItemData(
            repo = GithubRepo(
                "LittleText",
                "url"
            ), downloadBtnCallback = {}, linkBtnCallback = {})
    )
}