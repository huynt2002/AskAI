package com.example.retrofit.presentation.view.setting_view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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
import com.example.retrofit.ui.theme.RetrofitTheme

@Composable
fun SettingView(toAccountSettingView: () -> Unit, viewModel: SettingViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(context, viewModel) {
        viewModel.event.collect {
            when (it) {
                is SettingViewNavigationEvent.ToAccountSettingView -> toAccountSettingView()
            }
        }
    }
    SettingScreen(state = state, onAction = viewModel::onAction)
}

@Composable
fun SettingScreen(state: SettingViewState, onAction: (SettingViewAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.setting_label),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    onClick = { onAction(SettingViewAction.AccountSetting) },
                ) {
                    Text("Account Setting")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RetrofitTheme { SettingScreen(state = SettingViewState(), onAction = {}) }
}
