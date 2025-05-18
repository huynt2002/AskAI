package com.example.retrofit.di

import android.content.Context
import androidx.room.Room
import com.example.retrofit.domain.repository.local_database.LocalDatabaseRepository
import com.example.retrofit.data.local_database.database.Database
import com.example.retrofit.data.local_database.LocalDatabaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDataBaseModule {
    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): Database =
        Room.databaseBuilder(context = context, Database::class.java, "local_db").build()

    @Singleton @Provides fun conversationDao(database: Database) = database.conversationDao()

    @Singleton @Provides fun messageDao(database: Database) = database.messageDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataBaseImplModule {
    @Singleton
    @Binds
    abstract fun localDatabaseImpl(localDatabaseImpl: LocalDatabaseImpl): LocalDatabaseRepository
}
