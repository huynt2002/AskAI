package com.example.retrofit.data.remote_database

import com.example.retrofit.data.remote_database.model.ConversationRequest
import com.example.retrofit.data.remote_database.model.ConversationResponse
import com.example.retrofit.data.remote_database.model.MessageRequest
import com.example.retrofit.data.remote_database.model.MessageResponse

interface RemoteDatabaseRepository {
    suspend fun getConversations(): List<ConversationResponse>

    suspend fun addConversation(conversation: ConversationRequest): ConversationResponse

    suspend fun updateConversation(conversation: ConversationRequest): ConversationResponse

    suspend fun addMessage(conversationId: String, message: MessageRequest): MessageResponse

    suspend fun getMessages(conversationId: String): List<MessageResponse>
}
