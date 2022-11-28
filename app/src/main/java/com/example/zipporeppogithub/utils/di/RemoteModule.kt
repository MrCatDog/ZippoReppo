package com.example.zipporeppogithub.utils.di

import com.example.zipporeppogithub.model.errorhandlers.NetworkErrorHandler
import com.example.zipporeppogithub.model.network.GithubApi
import com.example.zipporeppogithub.utils.BASE_URL
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class RemoteModule {

    @Singleton
    @Provides
    fun provideGson(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(gson: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gson)
        .build()

    @Singleton
    @Provides
    fun provideServerApi(retrofit: Retrofit): GithubApi = retrofit.create(GithubApi::class.java)

    @Provides
    fun provideNetworkErrorHandler(): NetworkErrorHandler = NetworkErrorHandler()
}