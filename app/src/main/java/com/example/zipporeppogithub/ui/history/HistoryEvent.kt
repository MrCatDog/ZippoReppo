package com.example.zipporeppogithub.ui.history

import com.example.zipporeppogithub.model.db.HistoryRecord

sealed class HistoryEvent {
    object ReposLoading : HistoryEvent()
    data class SetError(val errorMsgResource: Int) : HistoryEvent()
    data class RecordsFound(val repos: List<HistoryRecord>) : HistoryEvent()
    object RecordsEmpty : HistoryEvent()
}
