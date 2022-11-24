package com.example.zipporeppogithub

import android.app.Application
import android.content.Context
import com.example.zipporeppogithub.utils.di.AppComponent
import com.example.zipporeppogithub.utils.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initializeDagger()
    }

    private fun initializeDagger() {
        appComponent = DaggerAppComponent.builder()
            .applicationContext(applicationContext)
            .build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is App -> appComponent
        else -> applicationContext.appComponent
    }
