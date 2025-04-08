package com.example.retrofit.presentation.view.chat_view

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.example.retrofit.R
import com.example.retrofit.data.ai.model.Role
import com.example.retrofit.domain.impl.ai.FakeRepository
import com.example.retrofit.domain.impl.remote.remote_database.FakeRemoteDatabase
import me.huynt204567.android_ui_kit.MessageConfig
import me.huynt204567.android_ui_kit.MessageType
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
    val context = LocalContext.current
    val pickMedia =
        rememberLauncherForActivityResult(PickVisualMedia()) { uri -> viewModel.onReceiveUri(uri) }

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
                    val trailing = message.user == Role.USER
                    val config = MessageConfig(trailing = trailing, userAvatar = null)
                    if (message.imageUri != null) {
                        MessageView(
                            messageType =
                                MessageType.ImagePainter(
                                    rememberAsyncImagePainter(message.imageUri)
                                ),
                            messageConfig = config,
                        )
                    }

                    if (message.imageUrl != null) {
                        MessageView(
                            messageType =
                                MessageType.ImagePainter(
                                    rememberAsyncImagePainter(message.imageUrl)
                                ),
                            messageConfig = config,
                        )
                    }

                    if (message.text.isNotBlank()) {
                        MessageView(
                            messageType = MessageType.Text(message.text),
                            messageConfig = config,
                        )
                    }
                }
            }
            Column(
                modifier =
                    Modifier.fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background)
                        .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                AnimatedVisibility(visible = uiState.imageUri != null) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                    Modifier.size(100.dp, 120.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceContainerHigh,
                                            shape = MaterialTheme.shapes.small,
                                        )
                                        .clip(MaterialTheme.shapes.small),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(uiState.imageUri),
                                    null,
                                    Modifier.sizeIn(maxWidth = 100.dp, maxHeight = 120.dp),
                                )
                                IconButton(
                                    onClick = { viewModel.onImageSelect(null) },
                                    Modifier.align(Alignment.TopEnd).size(24.dp),
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }

                        HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    IconButton(
                        onClick = {
                            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                        }
                    ) {
                        Icon(painterResource(R.drawable.image_icon), "select_image")
                    }

                    OutlinedTextField(
                        value = uiState.text,
                        onValueChange = { it -> viewModel.onTextChange(it) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )

                    IconButton(
                        onClick = { viewModel.onClick(context) },
                        enabled = !uiState.isAiGenerating,
                    ) {
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

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    val viewModel =
        ChatViewModel(
            aiRepository = FakeRepository(),
            savedStateHandle = SavedStateHandle(),
            //localDatabaseRepository = FakeLocalDatabaseImpl(),
            remoteDatabaseRepository = FakeRemoteDatabase(),
        )
    ChatView(null, {}, viewModel)
}
