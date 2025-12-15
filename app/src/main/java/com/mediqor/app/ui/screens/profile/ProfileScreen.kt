package com.mediqor.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mediqor.app.ui.screens.profile.model.User
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag

@Composable
fun ProfileScreen(userViewModel: UserViewModel, navController: NavController? = null) {
    val user by userViewModel.user.collectAsState()
    val loading by userViewModel.loading.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Background green gradient look (simple)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF00B16A)) // green - tweak to match screenshot
            .padding(16.dp)
    ) {
        // Header card with avatar + name + ePoints
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val photoUrl = user?.photoUrl
            if (!photoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Profile image",
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // initials circle
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initialsFromName(user?.name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00B16A)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = user?.name ?: "Guest User",
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                // ePoints badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF16A085).copy(alpha = 0.9f)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${user?.ePoints ?: 0} ePoints", color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Top card with 4 actions (Profile, Orders, Wishlist, Location)
        Surface(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            color = Color.White.copy(alpha = 0.95f)
        ) {
            Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                ActionItem(icon = Icons.Default.Person, label = "Profile") {
                    // you could navigate to profile details etc.
                }
                ActionItem(icon = Icons.Default.List, label = "Orders"){}
                ActionItem(icon = Icons.Default.Favorite, label = "Wishlist"){}
                ActionItem(icon = Icons.Default.LocationOn, label = "Location"){}
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grid of options like screenshot: 2 columns of cards
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OptionCard(icon = Icons.Default.List, label = "My Enquiries", modifier = Modifier.weight(1f)){}
                OptionCard(icon = Icons.Default.Star, label = "Medicine Reminder", modifier = Modifier.weight(1f)){}
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OptionCard(icon = Icons.Default.ShoppingBag, label = "Offers", modifier = Modifier.weight(1f)){}
                OptionCard(icon = Icons.Default.Person, label = "Security", modifier = Modifier.weight(1f)){}
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OptionCard(icon = Icons.Default.Settings, label = "Settings", modifier = Modifier.weight(1f)){}
                OptionCard(icon = Icons.Default.Star, label = "Rate Us", modifier = Modifier.weight(1f)){}
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OptionCard(icon = Icons.Default.Person, label = "Refer a Friend", modifier = Modifier.weight(1f)){}
                OptionCard(icon = Icons.Default.ExitToApp, label = "Logout", modifier = Modifier.weight(1f)) {
                    coroutineScope.launch {
                        FirebaseAuth.getInstance().signOut()
                        // After sign out, go back to auth route
                        navController?.navigate("auth") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                }
            }
        }

        // bottom padding to emulate space above bottom nav
        Spacer(modifier = Modifier.weight(1f))
    }

    if (loading) {
        // Mini overlay loading indicator (optional)
    }
}

@Composable
private fun ActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(28.dp), tint = Color(0xFF00B16A))
        }
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun OptionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(110.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color(0xFF00B16A))
                Spacer(modifier = Modifier.height(12.dp))
                Text(label, fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}

private fun initialsFromName(name: String?): String {
    if (name.isNullOrBlank()) return "SS"
    val parts = name.trim().split(" ")
    val first = parts.getOrNull(0)?.firstOrNull()?.uppercaseChar() ?: 'S'
    val second = parts.getOrNull(1)?.firstOrNull()?.uppercaseChar() ?: first
    return "$first$second"
}
