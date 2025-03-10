package com.example.retrofit.data

import com.example.retrofit.BuildConfig
import com.example.retrofit.data.model.RequestBody
import com.example.retrofit.data.model.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AiAPI {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String = BuildConfig.API_KEY,
        @Body requestBody: RequestBody,
    ): ResponseBody
}
