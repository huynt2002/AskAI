package com.example.retrofit.data.ai

import com.example.retrofit.data.ai.model.Message
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun getReply(message: Message): Message

    suspend fun getReply(listMessage: List<Message>):Message

    fun getStreamReply(message: Message): Flow<Message>

    suspend fun getConversationTitle(content: String): String
}
