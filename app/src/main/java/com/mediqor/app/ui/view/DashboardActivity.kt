package com.mediqor.app.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.mediqor.app.ui.screens.HomeScreen
import com.mediqor.app.ui.screens.NotificationScreen
import com.mediqor.app.ui.screens.CartScreen
import com.mediqor.app.ui.screens.profile.ProfileScreen
import com.mediqor.app.viewmodel.HomeViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {

    val context = LocalContext.current
    val activity = context as ComponentActivity
    val navController = rememberNavController()

    val navList = listOf(
        Triple("Home", Icons.Filled.Home, "home"),
        Triple("Cart", Icons.Filled.ShoppingCart, "cart"),
        Triple("Notify", Icons.Filled.Notifications, "notification"),
        Triple("Profile", Icons.Filled.Person, "profile")
    )

    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ecommerce") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0B8FAC)) {
                navList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.third) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.second, null) },
                        label = { Text(item.first) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.White.copy(0.6f),
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.White.copy(0.6f)
                        )
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { DashboardHomeScreen() }
            composable("cart") { CartScreen() }
            composable("notification") { NotificationScreen() } // <-- uses your separate screen
            composable("profile") { ProfileScreen() }
        }
    }
}

// Wrapper for HomeScreen with ViewModel
@Composable
fun DashboardHomeScreen() {
    val homeViewModel: HomeViewModel = viewModel()
    HomeScreen(viewModel = homeViewModel)
}
