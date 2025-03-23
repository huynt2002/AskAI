package com.example.retrofit.presentation.list_view

import com.example.retrofit.presentation.model.ConversationUI

sealed interface ListViewAction {
    data class ConversationClick(val conversationUI: ConversationUI) : ListViewAction

    object NewConversation : ListViewAction
}

sealed class ListViewNavigationEvent {
    data class ToChatView(val conversationId: String) : ListViewNavigationEvent()
}
