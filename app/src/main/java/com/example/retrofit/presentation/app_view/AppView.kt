package com.example.retrofit.presentation.app_view

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.retrofit.R
import com.example.retrofit.ui.theme.RetrofitTheme
import com.example.retrofit.util.Route
import me.auth_android.auth_kit.presentation.views.auth_view.AuthView
import me.auth_android.auth_kit.presentation.views.on_app_auth_view.AppReAuthView
import me.rolingo.core.ui.animation.enterPop
import me.rolingo.core.ui.animation.enterPush
import me.rolingo.core.ui.animation.exitPop
import me.rolingo.core.ui.animation.exitPush

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
            //            TabBarView(
            //                toSignInView = { navController.navigate(Route.Auth) },
            //                toReAuth = { navController.navigate(Route.ReAuth) },
            //            )
            //            NavigationRailView(
            //                toSignInView = { navController.navigate(Route.Auth) },
            //                toReAuth = { navController.navigate(Route.ReAuth) },
            //            )
            AdaptiveMainAppView(
                toSignIn = { navController.navigate(Route.Auth) },
                toReAuth = { navController.navigate(Route.ReAuth) },
            )
        }

        composable<Route.ReAuth>(enterTransition = enterPush, exitTransition = exitPush) {
            AppReAuthView { navController.navigateUp() }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    RetrofitTheme { AppView() }
}
