package com.example.zipporeppogithub.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryRecord(
    val userLogin: String,
    val repoName: String,
    val dateAndTime: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
