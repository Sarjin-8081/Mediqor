package com.example.mediqorog.view

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.view.screens.ChatbotScreen
import com.example.mediqorog.viewmodel.ChatbotViewModel
import com.example.mediqorog.view.screens.*

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
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val chatbotViewModel: ChatbotViewModel = viewModel()
    val showChatbot = chatbotViewModel.showChatbot.value
    val navigationItems = remember {
        listOf(
            NavigationItem("Home", Icons.Filled.Home, "home"),
            NavigationItem("Cart", Icons.Filled.ShoppingCart, "cart"),
            NavigationItem("Notify", Icons.Filled.Notifications, "notification"),
            NavigationItem("Profile", Icons.Filled.Person, "profile")
        )
    }
    var selectedTab by remember { mutableStateOf(0) }
    val primaryColor = Color(0xFF0B8FAC)
    val lightPrimaryColor = primaryColor.copy(alpha = 0.3f)
    val unselectedColor = Color.Gray
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        text = navigationItems[selectedTab].title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                navigationItems.forEachIndexed { index, item ->
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
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = primaryColor,
                            selectedTextColor = primaryColor,
                            indicatorColor = lightPrimaryColor,
                            unselectedIconColor = unselectedColor,
                            unselectedTextColor = unselectedColor
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
            when (selectedTab) {
                0 -> {
                    HomeScreen(
                        onChatbotClick = {
                            chatbotViewModel.openChatbot()
                        }
                    )
                }
                1 -> CartScreen()
                2 -> MapScreen()
                3 -> ProfileScreen()
            }
            if (showChatbot) {
                ChatbotScreen(
                    onBackClick = {
                        chatbotViewModel.closeChatbot()
                    }
                )
            }
        }
    }
}