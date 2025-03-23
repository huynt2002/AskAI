package com.example.retrofit.data.local_database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.retrofit.data.ai.model.Role
import com.example.retrofit.presentation.model.MessageUI

@Entity(
    tableName = "message",
    foreignKeys =
        [
            ForeignKey(
                entity = ConversationEntity::class, // Reference Conversation entity
                parentColumns = ["id"],
                childColumns = ["conversationId"],
                onDelete = ForeignKey.CASCADE, // Cascade delete if conversation is deleted
            )
        ],
)
data class MessageEntity(
    val content: String,
    val role: String,
    @ColumnInfo("time_stamp") val timeStamp: Long = System.currentTimeMillis(),
    @PrimaryKey val id: String, // Manually assigned String ID
    val conversationId: String, // Foreign key reference
)

fun MessageEntity.toMessageUI(): MessageUI {
    return MessageUI(id = this.id, content = this.content, isUser = this.role == Role.USER.id)
}
