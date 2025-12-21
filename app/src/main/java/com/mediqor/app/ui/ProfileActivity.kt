package com.mediqor.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.R

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen()
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .padding(16.dp)
    ) {


        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Profile Photo
                Image(
                    painter = painterResource(id = R.drawable.exp),
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(15.dp))

                Column {
                    Text(
                        text = "MedQor",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "MediQor@gmail.com",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    tint = Color.Gray,
                    contentDescription = "Edit"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SettingsCard(
            title = "Profile",
            onClick = {}
        )
        SettingsCard(
            title = "Orders",
            onClick = {}
        )
        SettingsCard(
            title = "Wishlist",
            onClick = {}
        )
        SettingsCard(
            title = "Location",
            onClick = {}
        )
        SettingsCard(
            title = "Medicine Reminder",
            onClick = {}
        )
        SettingsCard(
            title = "Offers",
            onClick = {}
        )
        SettingsCard(
            title = "Security",
            onClick = {}
        )
        SettingsCard(
            title = "Settings",
            onClick = {}
        )
        SettingsCard(
            title = "Rate Us",
            onClick = {}
        )
        SettingsCard(
            title = "Refer a Friend",
            onClick = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Logout (separated)
        SettingsCard(
            title = "Logout",
            titleColor = Color(0xFFFF5252),
            onClick = {}
        )
    }
}

@Composable
fun SettingsCard(
    title: String,
    titleColor: Color = Color.White,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                color = titleColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}
