package com.example.retrofit.presentation.on_app_auth_view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import me.auth_android.auth_kit.presentation.views.account_view.AccountView
import me.auth_android.auth_kit.presentation.views.on_app_auth_view.OnAppAuthView

@Serializable
private sealed interface OnAppAuthViewRoute {
    @Serializable object Account : OnAppAuthViewRoute

    @Serializable object ReAuth : OnAppAuthViewRoute
}

@Composable
fun OnAppAuthView(signOut: () -> Unit, onBack: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController, startDestination = OnAppAuthViewRoute.Account) {
        composable<OnAppAuthViewRoute.Account> {
            AccountView(
                toSignInView = signOut,
                onNavigateBack = onBack,
                toReAuth = { navController.navigate(OnAppAuthViewRoute.ReAuth) },
            )
        }

        composable<OnAppAuthViewRoute.ReAuth> { OnAppAuthView { navController.navigateUp() } }
    }
}
