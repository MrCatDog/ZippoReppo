package com.example.zipporeppogithub.ui.repos

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.network.GithubRepo
import com.example.zipporeppogithub.ui.repos.composepreview.RepoItemData
import com.example.zipporeppogithub.ui.repos.composepreview.RepoItemDataProvider
import kotlinx.coroutines.flow.distinctUntilChanged

private val PERMISSIONS_REQUIRED = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
private const val VISIBLE_THRESHOLD = 5

@Composable
fun ReposScreen(
    viewModel: ReposViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val askForPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted -> viewModel.setPermissionAnswer(isGranted) }
    val uriHandler = LocalUriHandler.current

    if (state.htmlLink != null) {
        LaunchedEffect(Unit) {
            uriHandler.openUri(state.htmlLink!!)
            viewModel.screenNavigateOut()
        }
    }
    if (state.isPermissionRequired) {
        LaunchedEffect(Unit) {
            askForPermission.launch(PERMISSIONS_REQUIRED)
        }
    }

    when {
        state.repos.isNotEmpty() -> {
            ReposList(
                state.repos,
                viewModel::downloadBtnClicked,
                viewModel::linkBtnClicked,
                viewModel::onScrolledToEnd
            )
        }
        state.isLoading -> {
            CircularLoadingIndicator()
        }
        state.errorMsg != null -> {
            ErrorView(errMsgResId = state.errorMsg!!, viewModel::retryBtnClicked) //todo !!
        }
    }
}

@Composable
fun CircularLoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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
fun ReposList(
    repos: List<GithubRepo>,
    onDownloadClick: (GithubRepo) -> Unit,
    onLinkClick: (GithubRepo) -> Unit,
    onScroll: () -> Unit
) {
    val lazyListState: LazyListState = rememberLazyListState()

    InfiniteListHandler(lazyListState, onLoadMore = onScroll)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyListState
    ) {
        items(repos) {
            RepoItem(
                RepoItemData(
                    repo = it,
                    onDownloadClick,
                    onLinkClick
                )
            )
        }
    }
}

@Composable
fun InfiniteListHandler(
    listState: LazyListState,
    buffer: Int = VISIBLE_THRESHOLD,
    onLoadMore: () -> Unit
) {
    val loadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItemsNumber - buffer)
        }
    }

    LaunchedEffect(loadMore) {
        snapshotFlow { loadMore.value }
            .distinctUntilChanged()
            .collect {
                onLoadMore()
            }
    }
}

@Preview
@Composable
fun RepoItem(
    @PreviewParameter(RepoItemDataProvider::class) buildData: RepoItemData
//    repo: GithubRepo,
//    downloadBtnCallback: (GithubRepo) -> Unit,
//    linkBtnCallback: (GithubRepo) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.repo_item_margin)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = buildData.repo.name,
                fontSize = dimensionResource(id = R.dimen.history_item_repo_title_size).value.sp
            )
            Button(onClick = { buildData.linkBtnCallback(buildData.repo) }) {
                Text(
                    text = stringResource(id = R.string.repo_open_in_browser_btn_text)
                )
            }
        }
        Button(
            onClick = { buildData.downloadBtnCallback(buildData.repo) }) {
            Icon(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.download_repo_img_size).value.dp),//todo нужно ли тут это .value.dp
                painter = painterResource(id = R.drawable.repo_download_img),
                contentDescription = stringResource(
                    id = R.string.repo_download_content_desc
                )
            )
        }
    }
}