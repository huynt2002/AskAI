package com.example.retrofit.domain.repository.local_database

import com.example.retrofit.data.local_database.database.model.ConversationEntity
import com.example.retrofit.data.local_database.database.model.MessageEntity

interface LocalDatabaseRepository {
    suspend fun getConversations(): List<ConversationEntity>

    suspend fun addNewConversation(conversation: ConversationEntity)

    suspend fun insertMessage(message: MessageEntity)

    suspend fun getMessagesInConversation(conversationId: String): List<MessageEntity>
}