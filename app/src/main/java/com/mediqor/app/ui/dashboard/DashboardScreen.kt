package com.mediqor.app.ui.dashboard

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mediqor.app.ui.navigation.Routes
import com.mediqor.app.ui.screens.*
import com.mediqor.app.ui.screens.profile.ProfileScreen

@Composable
fun DashboardScreen(
    rootNavController: NavHostController
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController = bottomNavController)
        }
    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.HOME) { HomeScreen() }
            composable(Routes.SEARCH) { SearchScreen() }
            composable(Routes.CART) { CartScreen() }
            composable(Routes.PROFILE) { ProfileScreen() }
        }
    }
}
