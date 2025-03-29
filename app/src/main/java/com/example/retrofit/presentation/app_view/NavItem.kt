package com.example.retrofit.presentation.app_view

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.retrofit.util.MainAppRoute

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val route: MainAppRoute,
    val onSelect: () -> Unit = {},
)
