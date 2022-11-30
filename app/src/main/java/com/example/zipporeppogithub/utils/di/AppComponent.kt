package com.example.zipporeppogithub.utils.di

import android.content.Context
import com.example.zipporeppogithub.ui.history.HistoryViewModel
import com.example.zipporeppogithub.ui.repos.ReposViewModelFactory
import com.example.zipporeppogithub.ui.search.SearchViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RemoteModule::class, DatabaseModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun applicationContext(applicationContext: Context): Builder
        fun build(): AppComponent
    }
    fun provideSearchViewModel(): SearchViewModel
    fun provideHistoryViewModel(): HistoryViewModel
    fun provideReposViewModelFactory(): ReposViewModelFactory
}