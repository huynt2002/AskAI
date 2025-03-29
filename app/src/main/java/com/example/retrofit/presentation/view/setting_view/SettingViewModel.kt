package com.example.retrofit.presentation.view.setting_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor() : ViewModel() {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(SettingViewState())
    val state =
        _state
            .onStart {
                if (!hasLoadedInitialData) {
                    /** Load initial data here * */
                    hasLoadedInitialData = true
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = SettingViewState(),
            )

    private val _event = Channel<SettingViewNavigationEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: SettingViewAction) {
        when (action) {
            is SettingViewAction.AccountSetting ->
                viewModelScope.launch {
                    _event.send(SettingViewNavigationEvent.ToAccountSettingView)
                }
        }
    }
}
