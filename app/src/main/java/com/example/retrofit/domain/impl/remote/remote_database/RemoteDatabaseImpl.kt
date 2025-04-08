package com.example.retrofit.domain.impl.remote.remote_database

import com.example.retrofit.data.remote_database.RemoteDatabaseApi
import com.example.retrofit.data.remote_database.RemoteDatabaseRepository
import com.example.retrofit.data.remote_database.model.ConversationRequest
import com.example.retrofit.data.remote_database.model.ConversationResponse
import com.example.retrofit.data.remote_database.model.MessageRequest
import com.example.retrofit.data.remote_database.model.MessageResponse
import java.time.Instant
import javax.inject.Inject
import me.auth_android.auth_kit.data.auth.defines.Authenticating

class RemoteDatabaseImpl
@Inject
constructor(
    private val remoteDatabaseApi: RemoteDatabaseApi,
    private val accountService: Authenticating,
) : RemoteDatabaseRepository {

    override suspend fun getConversations(): List<ConversationResponse> {
        val token = accountService.currentUser?.userIDToken()
        return remoteDatabaseApi.getConversations(makeHeader(token))
    }

    override suspend fun addConversation(conversation: ConversationRequest): ConversationResponse {
        val token = accountService.currentUser?.userIDToken()
        return remoteDatabaseApi.addConversation(authorization = makeHeader(token), conversation)
    }

    override suspend fun updateConversation(
        conversation: ConversationRequest
    ): ConversationResponse {
        val token = accountService.currentUser?.userIDToken()
        return remoteDatabaseApi.updateConversation(
            authorization = makeHeader(token),
            conversation.id.toString(),
            conversation,
        )
    }

    override suspend fun addMessage(
        conversationId: String,
        message: MessageRequest,
    ): MessageResponse {
        val token = accountService.currentUser?.userIDToken()
        return remoteDatabaseApi.saveMessage(
            authorization = makeHeader(token),
            conversationId,
            message,
        )
    }

    override suspend fun getMessages(conversationId: String): List<MessageResponse> {
        val token = accountService.currentUser?.userIDToken()
        return remoteDatabaseApi.getMessages(authorization = makeHeader(token), conversationId)
    }

    private fun makeHeader(token: String?): String {
        token?.let {
            return "Bearer $it"
        }
        return ""
    }
}

class FakeRemoteDatabase : RemoteDatabaseRepository {
    override suspend fun getConversations(): List<ConversationResponse> {
        return emptyList()
    }

    override suspend fun addConversation(conversation: ConversationRequest): ConversationResponse {
        return ConversationResponse(
            id = "1",
            title = "Hello",
            createAt = Instant.now(),
            updateAt = Instant.now(),
        )
    }

    override suspend fun updateConversation(
        conversation: ConversationRequest
    ): ConversationResponse {
        return ConversationResponse(
            id = "1",
            title = "Hello",
            createAt = Instant.now(),
            updateAt = Instant.now(),
        )
    }

    override suspend fun addMessage(
        conversationId: String,
        message: MessageRequest,
    ): MessageResponse {
        return MessageResponse("Hello", "", Instant.now(),"user")
    }

    override suspend fun getMessages(conversationId: String): List<MessageResponse> {
        return emptyList()
    }
}
