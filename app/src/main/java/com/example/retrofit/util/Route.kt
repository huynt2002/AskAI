package com.example.retrofit.util

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable object Auth : Route()

    @Serializable object MainApp : Route()

    @Serializable data class ChatView(val conversationId: String) : Route()

    @Serializable object AccountSetting : Route()

    @Serializable object ReAuth : Route()
}
