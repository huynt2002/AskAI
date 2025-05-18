package com.example.retrofit.domain.repository.ai

import com.example.retrofit.data.ai.model.MessageModel
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun getReply(messageModel: MessageModel): MessageModel

    suspend fun getReply(listMessageModel: List<MessageModel>): MessageModel

    fun getStreamReply(messageModel: MessageModel): Flow<MessageModel>

    suspend fun getConversationTitle(content: String): String
}