package com.example.retrofit.presentation.view.list_view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.retrofit.R
import com.example.retrofit.presentation.model.ConversationUI
import com.example.retrofit.presentation.model.MessageUI
import com.example.retrofit.ui.theme.RetrofitTheme
import java.util.UUID

@Composable
fun ListView(
    hasFab: Boolean = true,
    toChatView: (String) -> Unit,
    viewModel: ListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(context, viewModel) {
        viewModel.navigateEvent.collect {
            when (it) {
                is ListViewNavigationEvent.ToChatView -> toChatView(it.conversationId)
            }
        }
    }
    ListScreen(hasFab = hasFab, state = state, onAction = viewModel::onAction)
    if (state.isLoading) {
        CircularProgressIndicator()
    }
}

@Composable
fun ListScreen(hasFab: Boolean = true, state: ListViewState, onAction: (ListViewAction) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(4.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(state.list.size) { item ->
                    ListItemView(conversation = state.list[item], onAction = onAction)
                }
            }
        }
        if (hasFab) {
            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                onClick = { onAction(ListViewAction.NewConversation) },
            ) {
                Icon(Icons.Default.Add, "")
            }
        }
    }
}

@Composable
private fun ListItemView(conversation: ConversationUI, onAction: (ListViewAction) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier =
            Modifier.fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small,
                )
                .clickable { onAction(ListViewAction.ConversationClick(conversation)) }
                .padding(horizontal = 8.dp),
    ) {
        Text(conversation.title)
        Text(text = "...", modifier = Modifier.padding(start = 8.dp))
    }
}