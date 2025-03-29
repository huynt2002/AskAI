package com.example.retrofit.data.local_database.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.retrofit.presentation.model.ConversationUI

@Entity(tableName = "conversation")
data class ConversationEntity(
    @PrimaryKey val id: String, // Make sure this matches `conversationId`
    val title: String,
    @ColumnInfo("time_stamp") val timeStamp: Long = System.currentTimeMillis(),
)

fun ConversationEntity.toConversationUI(): ConversationUI {
    return ConversationUI(id = this.id, title = this.title, listOf())
}
