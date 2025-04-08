package com.example.retrofit.di

import android.content.Context
import com.example.retrofit.BuildConfig
import com.example.retrofit.R
import com.example.retrofit.data.ai.AiAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    @Singleton
    @Provides
    fun api(): AiAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://generativelanguage.googleapis.com/")
            .build()
            .create(AiAPI::class.java)
    }

    @Singleton @Provides fun okHttpClient(): OkHttpClient = OkHttpClient()
}