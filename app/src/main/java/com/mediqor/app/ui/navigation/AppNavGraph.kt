package com.mediqor.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mediqor.app.ui.dashboard.DashboardScreen
import com.mediqor.app.ui.screens.LoginScreen
import com.mediqor.app.ui.screens.SplashScreen

@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(navController)
        }
    }
}
