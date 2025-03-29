package com.example.retrofit.presentation.view.chat_view

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofit.data.ai.AiRepository
import com.example.retrofit.data.ai.model.Role
import com.example.retrofit.data.ai.model.toMessageUI
import com.example.retrofit.data.local_database.LocalDatabaseRepository
import com.example.retrofit.data.local_database.database.model.ConversationEntity
import com.example.retrofit.data.local_database.database.model.MessageEntity
import com.example.retrofit.data.local_database.database.model.toMessageUI
import com.example.retrofit.presentation.model.ConversationUI
import com.example.retrofit.presentation.model.MessageUI
import com.example.retrofit.presentation.model.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.plus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val aiRepository: AiRepository,
    private val localDatabaseRepository: LocalDatabaseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ViewState>(ViewState())
    private var id = savedStateHandle["conversationId"] ?: ""
    val uiState =
        _uiState
            .onStart {
                if (id.isNotBlank()) {
                    val list: List<MessageUI> =
                        localDatabaseRepository.getMessagesInConversation(id).map {
                            it.toMessageUI()
                        }
                    _uiState.update { state ->
                        state.copy(conversationUI = state.conversationUI.copy(listMessage = list))
                    }
                }
            }
            .stateIn(
                viewModelScope,
                initialValue = ViewState(),
                started = SharingStarted.WhileSubscribed(5000L),
            )

    fun onTextChange(text: String) {
        _uiState.update { it.copy(text = text) }
    }

    fun onClick() {
        if (_uiState.value.text.trim().isBlank()) {
            return
        }

        val sendMessage = MessageUI(content = _uiState.value.text.trim(), isUser = true)
        _uiState.update {
            it.copy(
                conversationUI =
                    ConversationUI(
                        it.conversationUI.id,
                        it.conversationUI.title,
                        it.conversationUI.listMessage + sendMessage,
                    )
            )
        }
        addMessage(sendMessage)
        onTextChange("")
        _uiState.update { it.copy(isAiGenerating = true) }
        viewModelScope.launch(Dispatchers.IO) {
            // val message = aiRepository.getReply(sendMessage.toMessage()) //oneLine
            val message =
                aiRepository.getReply(
                    _uiState.value.conversationUI.listMessage.map { it.toMessage() }
                )
            val messageUI = message.toMessageUI()
            _uiState.update {
                it.copy(
                    conversationUI =
                        ConversationUI(
                            it.conversationUI.id,
                            it.conversationUI.title,
                            it.conversationUI.listMessage + messageUI,
                        )
                )
            }

            //            aiRepository.getStreamReply(sendMessage.toMessage()).collect { message ->
            //                _uiState.update { it.copy(
            //                    conversationUI =
            //                        ConversationUI(
            //                            it.conversationUI.id,
            //                            it.conversationUI.title,
            //                            it.conversationUI.listMessage + message.toMessageUI(),
            //                        )
            //                ) }
            //            }
            addMessage(messageUI = messageUI)
            _uiState.update { it.copy(isAiGenerating = false) }
        }
    }

    fun addMessage(messageUI: MessageUI) {
        viewModelScope.launch(Dispatchers.IO) {
            if (id.isBlank()) {
                id = UUID.randomUUID().toString()
                val title = aiRepository.getConversationTitle(messageUI.content)
                localDatabaseRepository.addNewConversation(
                    ConversationEntity(id = id, title = title)
                )
            }
            val messageEntity =
                MessageEntity(
                    content = messageUI.content,
                    role = (if (messageUI.isUser) Role.USER else Role.MODEL).id,
                    id = UUID.randomUUID().toString(),
                    conversationId = id,
                )
            localDatabaseRepository.insertMessage(messageEntity)
        }
    }
}
