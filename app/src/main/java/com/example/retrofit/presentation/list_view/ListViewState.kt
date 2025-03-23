package com.example.retrofit.presentation.list_view

import androidx.compose.runtime.Immutable
import com.example.retrofit.presentation.model.ConversationUI

@Immutable
data class ListViewState(
    val list: List<ConversationUI> = emptyList(),
    val isLoading: Boolean = false,
)
