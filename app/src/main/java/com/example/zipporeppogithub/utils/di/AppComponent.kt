package com.example.zipporeppogithub.utils.di

import android.content.Context
import com.example.zipporeppogithub.viewmodels.HistoryViewModel
import com.example.zipporeppogithub.viewmodels.ReposViewModelFactory
import com.example.zipporeppogithub.viewmodels.SearchViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
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