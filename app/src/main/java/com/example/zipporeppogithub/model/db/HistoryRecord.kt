package com.example.zipporeppogithub.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.zipporeppogithub.utils.DATE_FORMAT
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "history")
data class HistoryRecord(
    val userLogin: String,
    val repoName: String,
    val dateAndTime: Date
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun getFormattedDate(): String =
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(dateAndTime)
}