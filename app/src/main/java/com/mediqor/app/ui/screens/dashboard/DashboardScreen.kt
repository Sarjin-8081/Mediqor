package com.mediqor.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mediqor.app.ui.navigation.BottomNavItem
import com.mediqor.app.ui.navigation.bottomNavItems
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mediqor.app.ui.screens.home.HomeScreen
import com.mediqor.app.ui.screens.featured.FeaturedScreen
import com.mediqor.app.ui.screens.package_.PackageScreen
import com.mediqor.app.ui.screens.cart.CartScreen
import com.mediqor.app.ui.screens.chat.ChatScreen
import com.mediqor.app.ui.screens.profile.ProfileScreen
import com.mediqor.app.ui.screens.profile.UserViewModel

@Composable
fun DashboardScreen(navController: NavController, userViewModel: UserViewModel) {
    val innerNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController = innerNavController)
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(navController = innerNavController, startDestination = BottomNavItem.Home.route) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen()
                }
                composable(BottomNavItem.Featured.route) {
                    FeaturedScreen()
                }
                composable(BottomNavItem.Package.route) {
                    PackageScreen()
                }
                composable(BottomNavItem.Cart.route) {
                    CartScreen()
                }
                composable(BottomNavItem.Chat.route) {
                    ChatScreen()
                }
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(userViewModel = userViewModel, navController = navController)
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { androidx.compose.material3.Text(item.title) }
            )
        }
    }
}
