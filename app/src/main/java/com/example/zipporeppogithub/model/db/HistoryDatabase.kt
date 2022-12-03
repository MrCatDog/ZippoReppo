package com.example.zipporeppogithub.model.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HistoryRecord::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
