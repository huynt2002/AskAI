package com.example.retrofit.presentation.view.chat_view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retrofit.R
import com.example.retrofit.domain.impl.ai.FakeRepository
import com.example.retrofit.domain.impl.local_database.FakeLocalDatabaseImpl
import com.example.retrofit.presentation.model.toMessageViewType
import me.huynt204567.android_ui_kit.MessageConfig
import me.huynt204567.android_ui_kit.MessageView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatView(
    conversationId: String?,
    onBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.conversationUI.listMessage.size) { listState.animateScrollToItem(0) }
    Column(
        modifier =
            Modifier.padding(horizontal = 12.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "") }
        }
        Column(modifier = Modifier.imePadding()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 16.dp),
                reverseLayout = true,
                state = listState,
            ) {
                items(items = uiState.conversationUI.listMessage.reversed(), key = { it.id }) {
                    message ->
                    val trailing = message.isUser

                    MessageView(
                        messageType = message.messageUIType.toMessageViewType(),
                        messageConfig = MessageConfig(trailing = trailing, userAvatar = null),
                    )
                }
            }

            Row(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = uiState.text,
                    onValueChange = { it -> viewModel.onTextChange(it) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )

                IconButton(onClick = viewModel::onClick, enabled = !uiState.isAiGenerating) {
                    if (uiState.isAiGenerating) {
                        CircularProgressIndicator()
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.baseline_send_24),
                            "",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    val viewModel =
        ChatViewModel(
            aiRepository = FakeRepository(),
            savedStateHandle = SavedStateHandle(),
            localDatabaseRepository = FakeLocalDatabaseImpl(),
        )
    ChatView(null, {}, viewModel)
}
