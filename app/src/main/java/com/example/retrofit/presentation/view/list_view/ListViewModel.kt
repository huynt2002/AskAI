package com.example.retrofit.presentation.view.list_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofit.data.local_database.LocalDatabaseRepository
import com.example.retrofit.data.local_database.database.model.toConversationUI
import com.example.retrofit.presentation.model.ConversationUI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListViewModel
@Inject
constructor(private val localDatabaseRepository: LocalDatabaseRepository) : ViewModel() {
    private val _state = MutableStateFlow(ListViewState())
    val state =
        _state
            .onStart {
                _state.update { it.copy(isLoading = true) }
                val conversationList: List<ConversationUI> =
                    localDatabaseRepository.getConversations().map { it.toConversationUI() }
                _state.update { ListViewState(conversationList, isLoading = false) }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = ListViewState(),
            )

    private val _navigateEvent = Channel<ListViewNavigationEvent>()
    val navigateEvent = _navigateEvent.receiveAsFlow()

    fun onAction(action: ListViewAction) {
        viewModelScope.launch {
            when (action) {
                is ListViewAction.ConversationClick -> {
                    _navigateEvent.send(
                        ListViewNavigationEvent.ToChatView(action.conversationUI.id)
                    )
                }
                is ListViewAction.NewConversation -> {
                    _navigateEvent.send(ListViewNavigationEvent.ToChatView(""))
                }
            }
        }
    }
}
