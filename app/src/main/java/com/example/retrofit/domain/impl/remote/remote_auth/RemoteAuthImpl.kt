package com.example.retrofit.domain.impl.remote.remote_auth

import com.example.retrofit.data.auth.RemoteAuthApi
import com.example.retrofit.data.auth.RemoteAuthRepository
import java.util.concurrent.CancellationException
import javax.inject.Inject
import me.auth_android.auth_kit.data.auth.defines.Authenticating

class RemoteAuthImpl
@Inject
constructor(private val authApi: RemoteAuthApi, private val authenticating: Authenticating) :
    RemoteAuthRepository {
    override suspend fun saveUser() {
        try {
            val token = authenticating.currentUser?.userIDToken()?.let { "Bearer $it" } ?: return
            authApi.saveUser(token)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()
        }
    }
}
