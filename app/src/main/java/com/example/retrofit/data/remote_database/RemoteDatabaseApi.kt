package com.example.retrofit.data.remote_database

import com.example.retrofit.data.remote_database.model.ConversationRequest
import com.example.retrofit.data.remote_database.model.ConversationResponse
import com.example.retrofit.data.remote_database.model.MessageRequest
import com.example.retrofit.data.remote_database.model.MessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteDatabaseApi {
    @GET("conversations")
    suspend fun getConversations(
        @Header("Authorization") authorization: String
    ): List<ConversationResponse>

    @POST("conversations")
    suspend fun addConversation(
        @Header("Authorization") authorization: String,
        @Body body: ConversationRequest,
    ): ConversationResponse

    @PUT("conversations/{id}")
    suspend fun updateConversation(
        @Header("Authorization") authorization: String,
        @Path("id") id: String,
        @Body body: ConversationRequest,
    ): ConversationResponse

    @GET("messages")
    suspend fun getMessages(
        @Header("Authorization") authorization: String,
        @Query("conversationId") conversationId: String,
    ): List<MessageResponse>

    @POST("messages")
    suspend fun saveMessage(
        @Header("Authorization") authorization: String,
        @Query("conversationId") conversationId: String,
        @Body body: MessageRequest,
    ): MessageResponse
}
