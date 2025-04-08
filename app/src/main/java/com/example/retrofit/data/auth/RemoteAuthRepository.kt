package com.example.retrofit.data.auth

interface RemoteAuthRepository {
    suspend fun saveUser()
}
