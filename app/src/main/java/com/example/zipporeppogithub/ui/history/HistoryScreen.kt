package com.example.zipporeppogithub.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import com.example.zipporeppogithub.R
import com.example.zipporeppogithub.model.db.HistoryRecord

@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel
) {
    val state by historyViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        historyViewModel.getHistory()
    }

    when {
        state.historyRecords.isNotEmpty() -> {
            HistoryRecords(records = state.historyRecords)
        }
        state.isLoading -> {
            CircularLoadingIndicator()
        }
        state.errorMsg != null -> {
            ErrorView(errMsgResId = state.errorMsg!!, historyViewModel::getHistory)
        }
        state.isEmpty -> {
            NothingFoundMsg()
        }
    }
}

@Composable
fun HistoryRecords(
    records: List<HistoryRecord>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(records) {
            HistoryRecordItem(it)
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
fun NothingFoundMsg() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.empty_history_text))
    }
}

@Preview
@Composable
fun HistoryRecordItem(@PreviewParameter(HistoryRecordProvider::class) historyRecord: HistoryRecord) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.history_item_margin)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = historyRecord.userLogin,
                fontSize = dimensionResource(id = R.dimen.history_item_username_size).value.sp
            )
            Text(
                text = historyRecord.repoName,
                fontSize = dimensionResource(id = R.dimen.history_item_repo_title_size).value.sp
            )
            Text(
                text = historyRecord.dateAndTime,
                fontSize = dimensionResource(id = R.dimen.history_item_date_size).value.sp
            )
        }

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

class HistoryRecordProvider : PreviewParameterProvider<HistoryRecord> {
    override val values = sequenceOf(
        HistoryRecord("Firstborn", "IdontLikeThisParameterProvider", "simpleDateFormat"),
        HistoryRecord("JimTheWorm", "whoCares", "2023-02-13 01:11:33")
    )
}