package com.example.retrofit.presentation.app_view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.retrofit.presentation.view.chat_view.ChatView
import com.example.retrofit.presentation.view.list_view.ListView
import com.example.retrofit.presentation.view.setting_view.SettingView
import com.example.retrofit.util.MainAppRoute
import me.auth_android.auth_kit.presentation.views.account_view.AccountView

@Composable
fun TabBarView(toSignInView: () -> Unit, toReAuth: () -> Unit) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items =
        listOf(
            NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, MainAppRoute.ListView),
            NavItem(
                "Setting",
                Icons.Filled.Settings,
                Icons.Outlined.Settings,
                MainAppRoute.SettingView,
            ),
        )

    val navController = rememberNavController()
    NavHost(navController, startDestination = MainAppRoute.MainView) {
        composable<MainAppRoute.MainView> {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        items.forEachIndexed { index, item ->
                            val selected = selectedItem == index
                            val icon = if (selected) item.selectedIcon else item.unSelectedIcon
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = item.title) },
                                label = { Text(item.title) },
                                selected = selected,
                                onClick = { selectedItem = index },
                            )
                        }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    AnimatedContent(selectedItem) {
                        when (it) {
                            0 ->
                                ListView(
                                    toChatView = { id ->
                                        navController.navigate(MainAppRoute.ChatView(id))
                                    }
                                )

                            1 ->
                                SettingView(
                                    toAccountSettingView = {
                                        navController.navigate(MainAppRoute.AccountSetting)
                                    }
                                )
                        }
                    }
                }
            }
        }

        composable<MainAppRoute.AccountSetting> {
            AccountView(
                toSignInView = toSignInView,
                onNavigateBack = { navController.navigateUp() },
                toReAuth = toReAuth,
            )
        }

        composable<MainAppRoute.ChatView> { entry ->
            val id = entry.toRoute<MainAppRoute.ChatView>().conversationId
            Scaffold() { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                    ChatView(id, { navController.navigateUp() })
                }
            }
        }
    }
}
