package com.example.retrofit.presentation.chat_view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.retrofit.data.local_database.dao.FakeConversationDao
import com.example.retrofit.data.local_database.dao.FakeMessageDao
import com.example.retrofit.domain.impl.ai.FakeRepository

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
    Scaffold() { padding ->
        Column(
            modifier =
                Modifier.padding(padding)
                    .padding(horizontal = 12.dp)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "")
                }
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
                        MessageView(message.content, trailing = message.isUser)
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
}

@Composable
fun MessageView(message: String, trailing: Boolean = false, modifier: Modifier = Modifier) {
    val messageContent: @Composable () -> Unit = {
        val textColor =
            if (!trailing) MaterialTheme.colorScheme.onSecondary
            else MaterialTheme.colorScheme.onPrimary
        val backgroundColor =
            if (!trailing) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.primary
        val backgroundShape =
            if (!trailing)
                RoundedCornerShape(
                    bottomStart = 8.dp,
                    bottomEnd = 8.dp,
                    topStart = 0.dp,
                    topEnd = 8.dp,
                )
            else
                RoundedCornerShape(
                    bottomStart = 8.dp,
                    bottomEnd = 8.dp,
                    topStart = 8.dp,
                    topEnd = 0.dp,
                )
        Box(
            modifier =
                Modifier.background(color = backgroundColor, shape = backgroundShape)
                    .padding(12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = message, color = textColor)
        }
    }
    val messageBody: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment =
                if (!trailing) {
                    Alignment.Start
                } else {
                    Alignment.End
                },
        ) {
            messageContent()
        }
    }

    Box(modifier = modifier) {
        val messagePadding: PaddingValues =
            if (!trailing) {
                PaddingValues(start = 0.dp, end = 32.dp, top = 4.dp, bottom = 4.dp)
            } else {
                PaddingValues(start = 32.dp, end = 0.dp, top = 4.dp, bottom = 4.dp)
            }
        Row(
            modifier = Modifier.fillMaxWidth().padding(messagePadding),
            horizontalArrangement =
                if (!trailing) {
                    Arrangement.Start
                } else {
                    Arrangement.End
                },
        ) {
            if (!trailing) {
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) { messageBody() }
            } else {
                Box(modifier = Modifier.weight(1f)) { messageBody() }
                Spacer(Modifier.width(8.dp))
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
            messageDao = FakeMessageDao(),
            conversationDao = FakeConversationDao(),
        )
    ChatView(null, {}, viewModel)
}
