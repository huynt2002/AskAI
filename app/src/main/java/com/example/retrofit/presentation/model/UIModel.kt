package com.example.retrofit.presentation.model

import androidx.compose.runtime.Immutable
import com.example.retrofit.data.ai.model.Message
import com.example.retrofit.data.ai.model.Role
import java.util.UUID

@Immutable
data class ConversationUI(val id: String, val title: String, val listMessage: List<MessageUI>)


@Immutable
data class MessageUI(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
)

fun MessageUI.toMessage(): Message {
    return Message(this.content, if (this.isUser) Role.USER else Role.MODEL)
}