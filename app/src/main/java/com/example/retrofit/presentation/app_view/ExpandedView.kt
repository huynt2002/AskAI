package com.example.retrofit.presentation.app_view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.retrofit.presentation.view.chat_view.ChatView
import com.example.retrofit.presentation.view.list_view.ListView
import com.example.retrofit.presentation.view.setting_view.SettingView
import com.example.retrofit.util.MainAppRoute
import kotlinx.coroutines.launch
import me.auth_android.auth_kit.presentation.views.account_view.AccountView

@Composable
fun AdaptiveMainAppView(toSignIn: () -> Unit, toReAuth: () -> Unit) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val navigationType =
        when (windowSize.windowWidthSizeClass) {
            WindowWidthSizeClass.COMPACT -> NavigationType.BOTTOM_NAVIGATION
            WindowWidthSizeClass.MEDIUM -> NavigationType.NAVIGATION_RAIL
            WindowWidthSizeClass.EXPANDED -> NavigationType.NAVIGATION_DRAWER
            else -> NavigationType.BOTTOM_NAVIGATION // Default to bottom navigation
        }
    val navController = rememberNavController()

    val items =
        listOf(
            NavItem(
                "Home",
                Icons.Filled.Home,
                Icons.Outlined.Home,
                MainAppRoute.ListView,
                onSelect = { navController.navigate(MainAppRoute.ListView) },
            ),
            NavItem(
                "Setting",
                Icons.Filled.Settings,
                Icons.Outlined.Settings,
                MainAppRoute.SettingView,
                onSelect = { navController.navigate(MainAppRoute.SettingView) },
            ),
        )

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val selectedItem =
        items.find { item ->
            currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
        }
    AdaptiveNavigation(
        items = items,
        selectedItem = { selectedItem },
        navigationType = navigationType,
    ) { padding ->
        NavHost(navController, startDestination = MainAppRoute.ListView) {
            composable<MainAppRoute.ListView> {
                Box(modifier = Modifier.fillMaxHeight().padding(padding)) {
                    ListView(
                        toChatView = { id -> navController.navigate(MainAppRoute.ChatView(id)) }
                    )
                }
            }
            composable<MainAppRoute.SettingView> {
                Box(modifier = Modifier.fillMaxHeight().padding(padding)) {
                    SettingView(
                        toAccountSettingView = {
                            navController.navigate(MainAppRoute.AccountSetting)
                        }
                    )
                }
            }
            composable<MainAppRoute.ChatView> { entry ->
                val id = entry.toRoute<MainAppRoute.ChatView>().conversationId
                Box(modifier = Modifier.fillMaxHeight().padding(padding)) {
                    ChatView(id, { navController.navigateUp() })
                }
            }
            composable<MainAppRoute.AccountSetting> {
                AccountView(
                    toSignInView = toSignIn,
                    toReAuth = toReAuth,
                    onNavigateBack = { navController.navigateUp() },
                )
            }
        }
    }
}

@Composable
fun AdaptiveNavigation(
    items: List<NavItem>,
    selectedItem: () -> NavItem?,
    navigationType: NavigationType,
    content: @Composable ((PaddingValues) -> Unit),
) {
    val drawerState =
        rememberDrawerState(
            if (navigationType == NavigationType.NAVIGATION_DRAWER) DrawerValue.Open
            else DrawerValue.Closed
        )
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        if (drawerState.isOpen && navigationType != NavigationType.NAVIGATION_DRAWER)
            drawerState.close()
    }

    if (navigationType == NavigationType.NAVIGATION_DRAWER) {
        if (drawerState.isOpen) {
            PermanentNavigationDrawer(
                drawerContent = {
                    ModalDrawerSheet {
                        NavigationDrawerItem(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            icon = { Icon(Icons.Default.Menu, null) },
                            label = { Text("") },
                            selected = false,
                            onClick = { scope.launch { drawerState.close() } },
                        )
                        items.forEachIndexed { index, item ->
                            val selected = selectedItem() == item
                            val icon = if (selected) item.selectedIcon else item.unSelectedIcon
                            NavigationDrawerItem(
                                icon = { Icon(icon, contentDescription = item.title) },
                                label = { Text(item.title) },
                                selected = selected,
                                onClick = item.onSelect,
                                modifier = Modifier.padding(horizontal = 12.dp),
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    content(paddingValues)
                }
            }
        } else {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Row(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(visible = drawerState.isOpen == false) {
                        NavigationRail(
                            header = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, null)
                                }
                            }
                        ) {
                            items.forEachIndexed { index, item ->
                                val selected = selectedItem() == item
                                val icon = if (selected) item.selectedIcon else item.unSelectedIcon
                                NavigationRailItem(
                                    icon = { Icon(icon, contentDescription = item.title) },
                                    label = { Text(item.title) },
                                    selected = selected,
                                    onClick = item.onSelect,
                                )
                            }
                        }
                    }
                    content(innerPadding)
                }
            }
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    items.forEachIndexed { index, item ->
                        val selected = selectedItem() == item
                        val icon = if (selected) item.selectedIcon else item.unSelectedIcon
                        NavigationDrawerItem(
                            icon = { Icon(icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selected,
                            onClick = {
                                item.onSelect()
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }
            },
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    AnimatedVisibility(
                        visible =
                            navigationType == NavigationType.BOTTOM_NAVIGATION &&
                                items.any { it == selectedItem() }
                    ) {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                val selected = selectedItem() == item
                                val icon = if (selected) item.selectedIcon else item.unSelectedIcon
                                NavigationBarItem(
                                    icon = { Icon(icon, contentDescription = item.title) },
                                    label = { Text(item.title) },
                                    selected = selected,
                                    onClick = item.onSelect,
                                )
                            }
                        }
                    }
                },
            ) { innerPadding ->
                Row(modifier = Modifier.fillMaxSize()) {
                    AnimatedVisibility(visible = navigationType == NavigationType.NAVIGATION_RAIL) {
                        NavigationRail(
                            // modifier = Modifier.padding(top =
                            // innerPadding.calculateTopPadding()),
                            header = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, null)
                                }
                            }
                        ) {
                            items.forEachIndexed { index, item ->
                                val selected = selectedItem() == item
                                val icon = if (selected) item.selectedIcon else item.unSelectedIcon
                                NavigationRailItem(
                                    icon = { Icon(icon, contentDescription = item.title) },
                                    label = { Text(item.title) },
                                    selected = selected,
                                    onClick = item.onSelect,
                                )
                            }
                        }
                    }
                    content(innerPadding)
                }
            }
        }
    }
}

enum class NavigationType {
    BOTTOM_NAVIGATION,
    NAVIGATION_RAIL,
    NAVIGATION_DRAWER,
}
