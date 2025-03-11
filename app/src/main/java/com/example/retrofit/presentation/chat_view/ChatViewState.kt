package com.example.retrofit.presentation.chat_view

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.example.retrofit.data.model.Message
import com.example.retrofit.data.model.Role
import java.util.UUID

@Immutable
data class MessageUI(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
)

fun MessageUI.toMessage(): Message {
    return Message(this.content, if (this.isUser) Role.USER else Role.MODEL)
}

@Stable
data class ViewState(
    val text: String = "",
    val isAiGenerating: Boolean = false,
    val list: List<MessageUI> = listOf<MessageUI>(),
)
