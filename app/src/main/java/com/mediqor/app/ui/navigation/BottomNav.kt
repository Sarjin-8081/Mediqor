package com.mediqor.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home_tab", "Home", Icons.Default.Home)
    object Featured : BottomNavItem("featured_tab", "Featured", Icons.Default.Star)
    object Package : BottomNavItem("package_tab", "Package", Icons.Default.CardGiftcard)
    object Cart : BottomNavItem("cart_tab", "Cart", Icons.Default.ShoppingCart)
    object Chat : BottomNavItem("chat_tab", "Chat", Icons.Default.Chat)
    object Profile : BottomNavItem("profile_tab", "Profile", Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Featured,
    BottomNavItem.Package,
    BottomNavItem.Cart,
    BottomNavItem.Chat,
    BottomNavItem.Profile
)
