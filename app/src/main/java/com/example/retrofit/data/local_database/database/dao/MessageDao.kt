package com.example.retrofit.data.local_database.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retrofit.data.local_database.database.model.MessageEntity

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM message WHERE conversationId = :conversationId ORDER BY time_stamp ASC")
    suspend fun getMessagesInConversation(conversationId: String): List<MessageEntity>
}
