package com.example.zipporeppogithub.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "loads")
data class LoadingInfo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userLogin: String,
    val repoName: String,
    val dateAndTime: Date
)