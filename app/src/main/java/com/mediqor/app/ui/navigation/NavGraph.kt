package com.mediqor.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mediqor.app.ui.screens.HomeScreen
import com.mediqor.app.ui.screens.LoginScreen
import com.mediqor.app.ui.screens.SplashScreen
import com.mediqor.app.ui.screens.ForgotPasswordScreen



@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.HOME) {
            HomeScreen()
        }

        composable(Routes.FORGOTPASSWORD) {
            ForgotPasswordScreen(navController)
        }

    }
}
