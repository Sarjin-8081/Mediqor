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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.view.screens.*
import com.example.mediqorog.viewmodel.ChatbotViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardBody()
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody() {
    val chatbotViewModel: ChatbotViewModel = viewModel()
    val showChatbot by chatbotViewModel.showChatbot.collectAsState()
    var showAddProduct by remember { mutableStateOf(false) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val isAdmin = currentUser?.email?.contains("admin") == true

    val navigationItems = remember {
        listOf(
            NavigationItem("Home", Icons.Filled.Home, "home"),
            NavigationItem("Cart", Icons.Filled.ShoppingCart, "cart"),
            NavigationItem("Map", Icons.Filled.LocationOn, "map"),
            NavigationItem("Profile", Icons.Filled.Person, "profile")
        )
    }

    var selectedTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFF0B8FAC)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 11.sp) },
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Main content
            when (selectedTab) {
                0 -> HomeScreen(
                    onChatbotClick = { chatbotViewModel.openChatbot() },
                    onAddProductClick = { showAddProduct = true }
                )
                1 -> CartScreen()
                2 -> MapScreen()
                3 -> ProfileScreen()
            }

            // Chatbot overlay (full screen on top)
            if (showChatbot) {
                ChatbotScreen(
                    onBackClick = { chatbotViewModel.closeChatbot() }
                )
            }

            // Add Product overlay (full screen on top)
            if (showAddProduct) {
                AddProductScreen(
                    onBackClick = { showAddProduct = false }
                )
            }
        }
    }
}