package com.mediqor.app.ui.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.R
import androidx.compose.ui.tooling.preview.Preview

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgotPasswordBody()
        }
    }
}

@Composable
fun ForgotPasswordBody() {

    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = try { context as Activity } catch (e: Exception) { null }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Apply scaffold padding
                .padding(horizontal = 24.dp)
                .background(Color.White)
        ) {

            // Back arrow top-left
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopStart // Changed to TopStart for back arrow
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(40.dp) // More appropriate size for back arrow
                        .clickable {
                            activity?.finish()
                        }
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                "FORGOT PASSWORD",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                "Enter your registered email. We will send you reset instructions.",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(15.dp),
                placeholder = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF0B8FAC),
                    unfocusedIndicatorColor = Color(0xFFE0F0F5)
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Reset button
            Button(
                onClick = {
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Reset link sent (demo)", Toast.LENGTH_SHORT).show()
                        // Optionally finish activity after sending reset link
                        // activity?.finish()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text("SEND RESET LINK")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Back to login
            Text(
                "Back to Login",
                style = TextStyle(
                    color = Color(0xFF0B8FAC),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clickable {
                        activity?.finish()
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordPreview() {
    ForgotPasswordBody()
}