package com.example.retrofit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofit.data.model.toMessageUI
import com.example.retrofit.data.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ViewModel @Inject constructor(private val aiRepository: AiRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ViewState>(ViewState())
    val uiState = _uiState.asStateFlow()

    fun onTextChange(text: String) {
        _uiState.update { it.copy(text = text) }
    }

    fun onClick() {
        if (_uiState.value.text.trim().isBlank()) {
            return
        }
        val sendMessage = MessageUI(content = _uiState.value.text.trim(), isUser = true)
        _uiState.update { it.copy(list = it.list + sendMessage) }
        onTextChange("")
        _uiState.update { it.copy(isAiGenerating = true) }
        viewModelScope.launch(Dispatchers.IO) {

             val message = aiRepository.getReply(sendMessage.toMessage())
             _uiState.update { it.copy(list = it.list + message.toMessageUI()) }

//            aiRepository.getStreamReply(sendMessage.toMessage()).collect { message ->
//                _uiState.update { it.copy(list = it.list + message.toMessageUI()) }
//            }
            _uiState.update { it.copy(isAiGenerating = false) }
        }
    }
}
