package com.mediqor.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mediqor.app.ui.screens.CartScreen
import com.mediqor.app.ui.screens.HomeScreen
import com.mediqor.app.ui.screens.SearchScreen
import com.mediqor.app.ui.screens.profile.ProfileScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.Cart.route) { CartScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }
    }
}
