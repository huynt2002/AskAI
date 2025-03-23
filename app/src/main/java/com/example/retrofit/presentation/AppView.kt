package com.example.retrofit.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.retrofit.R
import com.example.retrofit.presentation.chat_view.ChatView
import com.example.retrofit.presentation.list_view.ListView
import com.example.retrofit.presentation.on_app_auth_view.OnAppAuthView
import com.example.retrofit.presentation.setting_view.SettingView
import com.example.retrofit.ui.theme.RetrofitTheme
import com.example.retrofit.util.Route
import me.auth_android.auth_kit.presentation.views.auth_view.AuthView
import me.rolingo.core.ui.animation.enterPop
import me.rolingo.core.ui.animation.enterPush
import me.rolingo.core.ui.animation.exitPop
import me.rolingo.core.ui.animation.exitPush

data class BottomNavItem(val title: String, val icon: ImageVector)

@Composable
fun AppView() {
    val navController = rememberNavController()
    val appName = stringResource(R.string.app_name)
    val appImage = painterResource(R.drawable.outline_question_answer_24)
    NavHost(navController = navController, startDestination = Route.Auth) {
        composable<Route.Auth> {
            AuthView(appName = appName, appImage = appImage) {
                navController.navigate(Route.MainApp) { popUpTo<Route.Auth> { inclusive = true } }
            }
        }
        composable<Route.MainApp>(enterTransition = enterPop, exitTransition = exitPop) {
            var selectedItem by rememberSaveable { mutableIntStateOf(0) }
            val items =
                listOf(
                    BottomNavItem("Home", Icons.Filled.Home),
                    BottomNavItem("Setting", Icons.Filled.Settings),
                )
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    AnimatedContent(selectedItem) {
                        when (it) {
                            0 -> ListView({ id -> navController.navigate(Route.ChatView(id)) })
                            1 -> SettingView({ navController.navigate(Route.AccountSetting) })
                        }
                    }
                }
            }
        }

        composable<Route.ChatView>(enterTransition = enterPush, exitTransition = exitPush) { entry
            ->
            val id = entry.toRoute<Route.ChatView>().conversationId
            ChatView(id, { navController.navigateUp() })
        }

        composable<Route.AccountSetting>(enterTransition = enterPush, exitTransition = exitPush) {
            OnAppAuthView(
                signOut = {
                    navController.navigate(Route.Auth) {
                        popUpTo<Route.AccountSetting> { inclusive = true }
                    }
                },
                onBack = { navController.navigateUp() },
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RetrofitTheme { AppView() }
}
