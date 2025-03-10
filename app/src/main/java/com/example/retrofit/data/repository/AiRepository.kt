package com.example.retrofit.data.repository

import com.example.retrofit.data.model.Message
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun getReply(message: Message): Message

    fun getStreamReply(message: Message): Flow<Message>
}
