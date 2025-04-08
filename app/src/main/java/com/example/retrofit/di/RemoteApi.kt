package com.example.retrofit.di

import com.example.retrofit.data.auth.RemoteAuthApi
import com.example.retrofit.data.remote_database.RemoteDatabaseApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object RemoteApi {
    private fun baseUrl(): String = "https://askaibackend-production.up.railway.app/"

    @Singleton
    @Provides
    fun remoteDatabaseApi(): RemoteDatabaseApi =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl())
            .build()
            .create(RemoteDatabaseApi::class.java)

    @Singleton
    @Provides
    fun remoteAuth(): RemoteAuthApi =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl())
            .build()
            .create(RemoteAuthApi::class.java)
}
