package com.example.retrofit.presentation.view.setting_view
sealed interface SettingViewAction {
    object AccountSetting : SettingViewAction
}

sealed interface SettingViewNavigationEvent {
    object ToAccountSettingView : SettingViewNavigationEvent
}
