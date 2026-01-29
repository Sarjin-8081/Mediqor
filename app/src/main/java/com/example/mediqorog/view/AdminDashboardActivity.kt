package com.example.mediqorog.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediqorog.screens.AddProductDialog
import com.example.mediqorog.screens.AdminProductsScreenContent
import com.example.mediqorog.view.screens.AdminHomeScreen
import com.example.mediqorog.view.screens.AdminSettingsScreen
import com.example.mediqorog.view.screens.AdminUsersScreenContent
import com.example.mediqorog.view.screens.AdminOrdersScreenContent


class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminDashboardScreen()
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun AdminDashboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF0B8FAC)

    val navItems = remember {
        listOf(
            BottomNavItem("home", "Home", Icons.Default.Home),
            BottomNavItem("products", "Products", Icons.Default.Inventory),
            BottomNavItem("orders", "Orders", Icons.Default.ShoppingCart),
            BottomNavItem("users", "Users", Icons.Default.People),
            BottomNavItem("settings", "Settings", Icons.Default.Settings)
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 12.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = primaryColor,
                            selectedTextColor = primaryColor,
                            indicatorColor = primaryColor.copy(alpha = 0.2f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            // Show FAB only on Products tab
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddProductDialog = true },
                    containerColor = primaryColor,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Product"
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> AdminHomeScreen(
                    onNavigateToProducts = { selectedTab = 1 },
                    onNavigateToOrders = { selectedTab = 2 },
                    onNavigateToSettings = { selectedTab = 4 }
                )
                1 -> AdminProductsScreenContent()
                2 -> AdminOrdersScreenContent()
                3 -> AdminUsersScreenContent()
                4 -> AdminSettingsScreen()
            }

            // Add Product Dialog/Screen
            if (showAddProductDialog) {
                AddProductDialog(
                    onDismiss = { showAddProductDialog = false },
                    onProductAdded = { showAddProductDialog = false }
                )
            }
        }
    }
}