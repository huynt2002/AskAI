package com.example.retrofit.util

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable object Auth : Route()

    @Serializable object MainApp : Route()

    @Serializable object ReAuth : Route()
}

sealed class MainAppRoute {
    @Serializable object MainView: MainAppRoute()

    @Serializable object ListView : MainAppRoute()

    @Serializable object SettingView : MainAppRoute()

    @Serializable data class ChatView(val conversationId: String) : MainAppRoute()

    @Serializable object AccountSetting : MainAppRoute()
}
