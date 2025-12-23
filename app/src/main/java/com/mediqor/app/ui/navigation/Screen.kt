package com.mediqor.app.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
}
