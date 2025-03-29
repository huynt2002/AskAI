package com.example.retrofit.data.local_database.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.retrofit.data.local_database.database.model.ConversationEntity

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversation ORDER BY time_stamp DESC")
    suspend fun getConversations(): List<ConversationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewConversation(conversation: ConversationEntity)
}