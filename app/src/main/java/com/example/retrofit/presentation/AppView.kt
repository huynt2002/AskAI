package com.example.retrofit.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.retrofit.R
import com.example.retrofit.presentation.chat_view.ChatView
import com.example.retrofit.util.Route
import me.auth_android.auth_kit.presentation.views.auth_view.AuthView

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
        composable<Route.MainApp> { ChatView() }

//        composable<Route.OnAppAuthView> {
//            OnAppAuthView(
//                toSignInView = {
//                    navController.navigate(Route.Auth) {
//                        popUpTo(Route.MainApp) { inclusive = true }
//                    }
//                },
//                onNavigateBack = { navController.navigateUp() },
//            )
//        }
    }
}
