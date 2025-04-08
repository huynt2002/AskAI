package com.example.retrofit.di

import android.content.Context
import com.example.retrofit.BuildConfig
import com.example.retrofit.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthKitService{
    @Singleton @Provides fun webClientId(): String = BuildConfig.WEB_CLIENT_KEY

    @Singleton
    @Provides
    fun termAndPolicyLinks(@ApplicationContext context: Context): List<String> =
        listOf(context.getString(R.string.term_link), context.getString(R.string.policy_link))
}
