package com.example.zipporeppogithub.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    fun insert(users: HistoryRecord)

    @Query("SELECT * FROM history ORDER BY id DESC") //или ORDER BY dateAndTime DESC ?
    fun getAllRecords(): List<HistoryRecord>

}
