package com.example.retrofit.domain.impl.local_database

import com.example.retrofit.data.local_database.LocalDatabaseRepository
import com.example.retrofit.data.local_database.database.dao.ConversationDao
import com.example.retrofit.data.local_database.database.dao.MessageDao
import com.example.retrofit.data.local_database.database.model.ConversationEntity
import com.example.retrofit.data.local_database.database.model.MessageEntity
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalDatabaseImpl
@Inject
constructor(private val conversationDao: ConversationDao, private val messageDao: MessageDao) :
    LocalDatabaseRepository {
    override suspend fun getConversations(): List<ConversationEntity> {
        return withContext(Dispatchers.IO) {
            try {
                conversationDao.getConversations()
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                emptyList()
            }
        }
    }

    override suspend fun addNewConversation(conversation: ConversationEntity) {
        withContext(Dispatchers.IO) {
            try {
                conversationDao.addNewConversation(conversation)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
            }
        }
    }

    override suspend fun insertMessage(message: MessageEntity) {
        withContext(Dispatchers.IO) {
            try {
                messageDao.insertMessage(message)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
            }
        }
    }

    override suspend fun getMessagesInConversation(conversationId: String): List<MessageEntity> {
        return withContext(Dispatchers.IO) {
            try {
                messageDao.getMessagesInConversation(conversationId)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                emptyList()
            }
        }
    }
}

class FakeLocalDatabaseImpl : LocalDatabaseRepository {
    override suspend fun getConversations(): List<ConversationEntity> {
        return emptyList()
    }

    override suspend fun addNewConversation(conversation: ConversationEntity) {}

    override suspend fun insertMessage(message: MessageEntity) {}

    override suspend fun getMessagesInConversation(conversationId: String): List<MessageEntity> {
        return emptyList()
    }
}
