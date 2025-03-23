package com.example.retrofit.di

import android.content.Context
import androidx.room.Room
import com.example.retrofit.data.local_database.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(context = context, Database::class.java, "local_db").build()

    @Singleton @Provides fun conversationDao(database: Database) = database.conversationDao()

    @Singleton @Provides fun messageDao(database: Database) = database.messageDao()
}
