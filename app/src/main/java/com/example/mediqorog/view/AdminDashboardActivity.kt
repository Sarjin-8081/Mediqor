package com.example.mediqorog.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mediqorog.view.screens.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AdminDashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdminDashboardApp(activity = this)
        }
    }
}

// Navigation Routes
object AdminRoutes {
    const val DASHBOARD = "dashboard"
    const val ORDERS = "orders"
    const val PRODUCTS = "products"
    const val PRESCRIPTIONS = "prescriptions"
    const val USERS = "users"
    const val ANALYTICS = "analytics"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
}

// Drawer Menu Items
data class DrawerMenuItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val badge: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardApp(activity: ComponentActivity) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val primaryColor = Color(0xFF0B8FAC)
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Drawer menu items
    val menuItems = listOf(
        DrawerMenuItem(AdminRoutes.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
        DrawerMenuItem(AdminRoutes.ORDERS, "Orders", Icons.Default.ShoppingCart, badge = 5),
        DrawerMenuItem(AdminRoutes.PRODUCTS, "Products", Icons.Default.Inventory),
        DrawerMenuItem(AdminRoutes.PRESCRIPTIONS, "Prescriptions", Icons.Default.Description, badge = 3),
        DrawerMenuItem(AdminRoutes.USERS, "Users", Icons.Default.People),
        DrawerMenuItem(AdminRoutes.ANALYTICS, "Analytics", Icons.Default.Analytics),
        DrawerMenuItem(AdminRoutes.NOTIFICATIONS, "Notifications", Icons.Default.Notifications, badge = 8),
        DrawerMenuItem(AdminRoutes.PROFILE, "Profile", Icons.Default.Person),
        DrawerMenuItem(AdminRoutes.SETTINGS, "Settings", Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(300.dp)
            ) {
                AdminDrawerContent(
                    menuItems = menuItems,
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        scope.launch {
                            drawerState.close()
                            navController.navigate(route) {
                                popUpTo(AdminRoutes.DASHBOARD) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onLogout = {
                        showLogoutDialog = true
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                AdminTopBar(
                    currentRoute = currentRoute,
                    onMenuClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AdminRoutes.DASHBOARD,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(AdminRoutes.DASHBOARD) {
                    AdminHomeScreen()
                }
                composable(AdminRoutes.ORDERS) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Orders Screen - Coming Soon")
                    }
                }
                composable(AdminRoutes.PRODUCTS) {
                    AdminProductsScreen()
                }
                composable(AdminRoutes.PRESCRIPTIONS) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Prescriptions Screen - Coming Soon")
                    }
                }
                composable(AdminRoutes.USERS) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Users Screen - Coming Soon")
                    }
                }
                composable(AdminRoutes.ANALYTICS) {
                    AdminAnalyticsScreen()
                }
                composable(AdminRoutes.NOTIFICATIONS) {
                    AdminNotificationsScreen()
                }
                composable(AdminRoutes.PROFILE) {
                    AdminProfileScreen()
                }
                composable(AdminRoutes.SETTINGS) {
                    AdminSettingsScreen()
                }
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Logout",
                    tint = Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    "Logout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text("Are you sure you want to logout from Admin Panel?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(activity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        activity.startActivity(intent)
                        activity.finish()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Logout", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color(0xFF0B8FAC))
                }
            }
        )
    }
}

@Composable
fun AdminDrawerContent(
    menuItems: List<DrawerMenuItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    val primaryColor = Color(0xFF0B8FAC)
    val currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Drawer Header - Modern Design
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(primaryColor)
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Admin Avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = primaryColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = currentUser?.displayName ?: "Admin",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = currentUser?.email ?: "admin@mediqor.com",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "ADMIN PANEL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

        // Menu Items
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            menuItems.forEach { item ->
                DrawerItem(
                    item = item,
                    isSelected = currentRoute == item.route,
                    onClick = { onItemClick(item.route) }
                )
            }
        }

        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))

        // Logout Button
        DrawerItem(
            item = DrawerMenuItem("logout", "Logout", Icons.Default.Logout),
            isSelected = false,
            onClick = onLogout,
            isLogout = true
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun DrawerItem(
    item: DrawerMenuItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    isLogout: Boolean = false
) {
    val primaryColor = Color(0xFF0B8FAC)
    val backgroundColor = if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) primaryColor else if (isLogout) Color.Red else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = item.title,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )

        // Badge for notifications
        item.badge?.let { count ->
            Surface(
                shape = CircleShape,
                color = Color.Red
            ) {
                Text(
                    text = if (count > 99) "99+" else count.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    currentRoute: String?,
    onMenuClick: () -> Unit
) {
    val title = when (currentRoute) {
        AdminRoutes.DASHBOARD -> "Dashboard"
        AdminRoutes.ORDERS -> "Orders Management"
        AdminRoutes.PRODUCTS -> "Products Management"
        AdminRoutes.PRESCRIPTIONS -> "Prescriptions Approval"
        AdminRoutes.USERS -> "Users Management"
        AdminRoutes.ANALYTICS -> "Analytics & Reports"
        AdminRoutes.NOTIFICATIONS -> "Notifications"
        AdminRoutes.PROFILE -> "Admin Profile"
        AdminRoutes.SETTINGS -> "Settings"
        else -> "Admin Panel"
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            // Quick Actions
            IconButton(onClick = { /* Notifications */ }) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ) {
                            Text("8", fontSize = 10.sp)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0B8FAC),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}