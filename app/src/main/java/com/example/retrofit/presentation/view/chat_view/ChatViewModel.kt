package com.example.retrofit.presentation.view.chat_view

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofit.data.ai.AiRepository
import com.example.retrofit.data.ai.model.MessageModel
import com.example.retrofit.data.ai.model.Role
import com.example.retrofit.data.ai.model.toMessageUI
import com.example.retrofit.data.remote_database.RemoteDatabaseRepository
import com.example.retrofit.data.remote_database.model.ConversationRequest
import com.example.retrofit.data.remote_database.model.MessageRequest
import com.example.retrofit.data.remote_database.model.toMessageUI
import com.example.retrofit.presentation.model.ConversationUI
import com.example.retrofit.presentation.model.MessageUI
import com.example.retrofit.util.ImageBase64Converter
import com.example.retrofit.util.ImageFileSaver
import dagger.hilt.android.lifecycle.HiltViewModel
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
    // private val localDatabaseRepository: LocalDatabaseRepository,
    private val remoteDatabaseRepository: RemoteDatabaseRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ViewState>(ViewState())
    private var id = savedStateHandle["conversationId"] ?: ""
    val uiState =
        _uiState
            .onStart {
                if (id.isNotBlank()) {
                    val list: List<MessageUI> =
                        // localDatabaseRepository.getMessagesInConversation(id).map { entity ->
                        //                                                    entity.toMessageUI()
                        //                                                }
                        remoteDatabaseRepository.getMessages(id).map { it.toMessageUI() }
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

    fun onImageSelect(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun onReceiveUri(uri: Uri?) {
        if (uri != null) {
            onImageSelect(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    fun onClick(context: Context) {
        val text = _uiState.value.text.trim()
        if (text.isBlank() && _uiState.value.imageUri == null) {
            return
        }

        val sendMessage =
            MessageUI(text = text, imageUri = _uiState.value.imageUri, user = Role.USER)

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
        onImageSelect(null)

        _uiState.update { it.copy(isAiGenerating = true) }
        viewModelScope.launch {
            launch { addMessageToLocalDatabase(context, sendMessage) }

            val messageJob =
                async(Dispatchers.IO) {
                    aiRepository.getReply(
                        _uiState.value.conversationUI.listMessage.map { messageUI ->
                            val base64String =
                                messageUI.imageUri?.let {
                                    async {
                                            ImageBase64Converter.imageUriToBase64(
                                                context,
                                                messageUI.imageUri,
                                            )
                                        }
                                        .await()
                                }

                            MessageModel(
                                text = messageUI.text,
                                imageBase64String = base64String,
                                user = messageUI.user,
                            )
                        }
                    )
                }

            val messageUI = messageJob.await().toMessageUI()
            launch { addMessageToLocalDatabase(context, messageUI) }

            _uiState.update {
                it.copy(
                    conversationUI =
                        it.conversationUI.copy(
                            listMessage = it.conversationUI.listMessage + messageUI
                        )
                )
            }
            _uiState.update { it.copy(isAiGenerating = false) }
        }
    }

    suspend fun addMessageToLocalDatabase(context: Context, messageUI: MessageUI) {
        val content = messageUI.text
        val conversationId =
            if (id.isBlank()) {
                val title = aiRepository.getConversationTitle(content)
                remoteDatabaseRepository
                    .addConversation(ConversationRequest(null, title = title))
                    .id
                //   localDatabaseRepository.addNewConversation(ConversationEntity(id = id, title =
                // title))
            } else id

        val imagePath = ImageFileSaver.saveImageUriToStorage(context, messageUI.imageUri) ?: ""

        val role =
            when (messageUI.user) {
                Role.MODEL -> "model"
                Role.USER -> "user"
            }
        remoteDatabaseRepository.addMessage(
            conversationId = conversationId,
            MessageRequest(content = messageUI.text, imagePath = "", role = role),
        )
        // localDatabaseRepository.insertMessage(MessageEntity(
        //                content = content,
        //                imagePath = imagePath,
        //                role = messageUI.user,
        //                id = UUID.randomUUID().toString(),
        //                conversationId = id,
        //            ))
    }
}
