package com.example.zipporeppogithub.utils.di

import android.content.Context
import androidx.room.Room
import com.example.zipporeppogithub.model.db.HistoryDao
import com.example.zipporeppogithub.model.db.HistoryDatabase
import com.example.zipporeppogithub.model.errorhandlers.DBErrorHandler
import com.example.zipporeppogithub.model.errorhandlers.ExternalStorageErrorHandler
import com.example.zipporeppogithub.utils.DATABASE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): HistoryDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            HistoryDatabase::class.java,
            DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun providePersonDao(db: HistoryDatabase): HistoryDao {
        return db.historyDao()
    }

    @Provides
    fun provideDBErrorHandler(): DBErrorHandler = DBErrorHandler()

    @Provides
    fun provideExternalErrorHandler(): ExternalStorageErrorHandler = ExternalStorageErrorHandler()

}
