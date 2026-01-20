// ============================================================
// CREATE NEW FILE: app/src/main/java/com/example/mediqorog/view/AdminDashboardActivity.kt
// ============================================================

package com.example.mediqorog.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.mediqorog.view.screens.AdminDashboardScreen
import com.example.mediqorog.view.screens.AdminOrdersScreen
import com.example.mediqorog.view.screens.AdminProductsScreen
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
                AdminDashboardScreen()
        }
    }
}

@Composable
fun AdminDashboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AdminNavItem(
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                AdminNavItem(
                    icon = Icons.Default.ShoppingCart,
                    label = "Orders",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                AdminNavItem(
                    icon = Icons.Default.Inventory,
                    label = "Products",
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                AdminNavItem(
                    icon = Icons.Default.People,
                    label = "Customers",
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> AdminDashboardScreen()
                1 -> AdminOrdersScreen()
                2 -> AdminProductsScreen()
                3 -> AdminCustomersScreen()
            }
        }
    }
}

@Composable
fun RowScope.AdminNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
fun AdminCustomersScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Customers Management",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Customer list and management coming soon...")
    }
}