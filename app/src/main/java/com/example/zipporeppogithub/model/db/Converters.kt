package com.example.zipporeppogithub.model.db

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun fromFriendsIdSet(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toFriendsIdSet(dateAsLong: Long): Date {
        return Date(dateAsLong)
    }

}
