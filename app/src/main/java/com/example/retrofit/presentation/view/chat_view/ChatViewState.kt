package com.example.retrofit.presentation.view.chat_view

import androidx.compose.runtime.Stable
import com.example.retrofit.presentation.model.ConversationUI
import java.util.UUID

@Stable
data class ViewState(
    val text: String = "",
    val isAiGenerating: Boolean = false,
    val conversationUI: ConversationUI =
        ConversationUI(id = UUID.randomUUID().toString(), "", listOf()),
)
