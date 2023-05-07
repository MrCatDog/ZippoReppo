package com.example.zipporeppogithub.ui.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
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
    val historyRecords = historyViewModel.historyRecords.observeAsState().value

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (historyRecords != null) {
            items(historyRecords) {
                HistoryRecordItem(it)
            }
        }
    }
}

@Preview
@Composable
fun HistoryRecordItem(@PreviewParameter(HistoryRecordProvider::class) historyRecord: HistoryRecord) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.history_item_margin)),
            horizontalAlignment = Alignment.CenterHorizontally) {
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

//android.content.res.Resources$NotFoundException: Could not resolve resource value...
//сломано в моём Electric Eel, говорят во фламинго починят...
class HistoryRecordProvider : PreviewParameterProvider<HistoryRecord> {
    override val values = sequenceOf(
        HistoryRecord("Firstborn", "IdontLikeThisParameterProvider", "simpleDateFormat"),
        HistoryRecord("JimTheWorm", "whoCares", "2023-02-13 01:11:33")
    )
}