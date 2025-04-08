package com.example.retrofit.di

import com.example.retrofit.data.ai.AiRepository
import com.example.retrofit.data.auth.RemoteAuthRepository
import com.example.retrofit.data.local_database.LocalDatabaseRepository
import com.example.retrofit.data.remote_database.RemoteDatabaseRepository
import com.example.retrofit.domain.impl.ai.AiRepositoryImp
import com.example.retrofit.domain.impl.local_database.LocalDatabaseImpl
import com.example.retrofit.domain.impl.remote.remote_auth.RemoteAuthImpl
import com.example.retrofit.domain.impl.remote.remote_database.RemoteDatabaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import me.auth_android.auth_kit.data.auth.defines.Authenticating
import me.auth_android.auth_kit.domain.firebase_auth.FirebaseAuthenticatingImp

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAiService(impl: AiRepositoryImp): AiRepository

    @Binds abstract fun provideAuthService(impl: FirebaseAuthenticatingImp): Authenticating

    @Singleton
    @Binds
    abstract fun localDatabaseImpl(localDatabaseImpl: LocalDatabaseImpl): LocalDatabaseRepository

    @Singleton
    @Binds
    abstract fun remoteDatabaseImpl(remoteDatabaseImpl: RemoteDatabaseImpl): RemoteDatabaseRepository

    @Singleton
    @Binds
    abstract fun remoteAuthImpl(remoteAuthRepository: RemoteAuthImpl): RemoteAuthRepository
}
