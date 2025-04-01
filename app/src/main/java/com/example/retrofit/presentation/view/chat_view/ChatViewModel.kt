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
import com.example.retrofit.presentation.model.MessageUIType
import com.example.retrofit.presentation.model.toMessageModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.plus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    fun onImageSelect(base64String: String) {
        _uiState.update { it.copy(base64Image = base64String) }
    }

    fun onClick() {
        if (_uiState.value.text.trim().isBlank()) {
            return
        }

        val sendMessage =
            MessageUI(
                messageUIType =
                    if (_uiState.value.base64Image.isBlank()) {
                        MessageUIType.Text(_uiState.value.text)
                    } else {
                        MessageUIType.Image(_uiState.value.text, _uiState.value.base64Image)
                    },
                isUser = true,
            )

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

        onTextChange("")
        onImageSelect("")

        _uiState.update { it.copy(isAiGenerating = true) }
        viewModelScope.launch {
            launch(Dispatchers.IO) { addMessageToLocalDatabase(sendMessage) }

            val messageJob =
                async(Dispatchers.IO) {
                    aiRepository.getReply(
                        _uiState.value.conversationUI.listMessage.map { it.toMessageModel() }
                    )
                }

            val messageUI = messageJob.await().toMessageUI()

            _uiState.update {
                it.copy(
                    conversationUI =
                        it.conversationUI.copy(
                            listMessage = it.conversationUI.listMessage + messageUI
                        )
                )
            }

            launch(Dispatchers.IO) { addMessageToLocalDatabase(messageUI) }

            _uiState.update { it.copy(isAiGenerating = false) }
        }
    }

    suspend fun addMessageToLocalDatabase(messageUI: MessageUI) {
        if (id.isBlank()) {
            id = UUID.randomUUID().toString()
            val content =
                when (messageUI.messageUIType) {
                    is MessageUIType.Image -> messageUI.messageUIType.content
                    is MessageUIType.Text -> messageUI.messageUIType.content
                }
            val title = aiRepository.getConversationTitle(content)
            localDatabaseRepository.addNewConversation(ConversationEntity(id = id, title = title))
        }
        val content =
            when (messageUI.messageUIType) {
                is MessageUIType.Text -> messageUI.messageUIType.content
                is MessageUIType.Image -> messageUI.messageUIType.base64ImageString
            }

        if (content.isBlank()) {
            return
        }

        val messageEntity =
            MessageEntity(
                content = content,
                imageString =
                    if (messageUI.messageUIType is MessageUIType.Image)
                        messageUI.messageUIType.base64ImageString
                    else "",
                role = (if (messageUI.isUser) Role.USER else Role.MODEL),
                id = UUID.randomUUID().toString(),
                conversationId = id,
            )
        localDatabaseRepository.insertMessage(messageEntity)
    }
}
