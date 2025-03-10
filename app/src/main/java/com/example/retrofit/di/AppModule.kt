package com.example.retrofit.di

import com.example.retrofit.data.AiAPI
import com.example.retrofit.data.repository.AiRepository
import com.example.retrofit.domain.AiRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAiService(impl: AiRepositoryImp): AiRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun api(): AiAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://generativelanguage.googleapis.com/")
            .build()
            .create(AiAPI::class.java)
    }

    @Singleton
    @Provides
    fun okHttpClient(): OkHttpClient = OkHttpClient()
}
