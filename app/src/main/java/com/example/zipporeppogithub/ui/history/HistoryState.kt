package com.example.zipporeppogithub.ui.history

import com.example.zipporeppogithub.model.db.HistoryRecord

data class HistoryState(
    val isLoading: Boolean,
    val historyRecords: List<HistoryRecord>,
    val errorMsg: Int?,
    val isEmpty: Boolean
) {
    companion object {
        fun initial() = HistoryState(
            isLoading = false,
            historyRecords = emptyList(),
            errorMsg = null,
            isEmpty = false
        )
    }
}
