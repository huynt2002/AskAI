package com.example.retrofit.presentation.model

import androidx.compose.runtime.Immutable

@Immutable
data class ConversationUI(val id: String, val title: String, val listMessage: List<MessageUI>)
