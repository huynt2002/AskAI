package com.example.retrofit.di

import android.content.Context
import com.example.retrofit.BuildConfig
import com.example.retrofit.R
import com.example.retrofit.data.ai.AiAPI
import com.example.retrofit.domain.repository.ai.AiRepository
import com.example.retrofit.data.ai.AiRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import me.auth_android.auth_kit.data.auth.defines.Authenticating
import me.auth_android.auth_kit.data.firebase_auth.FirebaseAuthenticatingImp
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAiService(impl: AiRepositoryImp): AiRepository

    @Binds abstract fun provideAuthService(impl: FirebaseAuthenticatingImp): Authenticating
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

    @Singleton @Provides fun okHttpClient(): OkHttpClient = OkHttpClient()
}

@Module
@InstallIn(SingletonComponent::class)
object AuthService{
    @Singleton @Provides fun webClientId(): String = BuildConfig.WEB_CLIENT_KEY

    @Singleton
    @Provides
    fun termAndPolicyLinks(@ApplicationContext context: Context): List<String> =
        listOf(context.getString(R.string.term_link), context.getString(R.string.policy_link))
}
