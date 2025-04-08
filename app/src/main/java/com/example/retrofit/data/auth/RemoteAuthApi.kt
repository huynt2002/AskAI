package com.example.retrofit.data.auth

import retrofit2.http.Header
import retrofit2.http.POST

interface RemoteAuthApi {
    @POST("users") suspend fun saveUser(@Header("Authorization") authorization: String)
}
