package com.mediqor.app.ui.view

import android.app.Activity
import android.content.Intent
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
import com.mediqor.app.ui.repository.UserRepoImpl
import com.mediqor.app.ui.viewmodel.UserViewModel

class ForgotPasswordActivity : ComponentActivity() {

    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repo = UserRepoImpl()
        viewModel = UserViewModel(repo)

        setContent {
            ForgotPasswordBody(viewModel = viewModel)
        }
    }
}

@Composable
fun ForgotPasswordBody(viewModel: UserViewModel? = null) {

    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .background(Color.White)
        ) {

            // Back arrow top-left
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopStart
            ) {
                IconButton(
                    onClick = { activity?.finish() },
                    enabled = !loading
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
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
                onValueChange = {
                    email = it
                    emailError = null // Clear error when user types
                },
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
                    unfocusedIndicatorColor = Color(0xFFE0F0F5),
                    errorIndicatorColor = Color.Red
                ),
                isError = emailError != null,
                supportingText = {
                    emailError?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                },
                enabled = !loading
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Reset button
            Button(
                onClick = {
                    // Validate email
                    if (email.isBlank()) {
                        emailError = "Please enter your email"
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Please enter a valid email address"
                        return@Button
                    }

                    // Send reset email
                    if (viewModel != null) {
                        loading = true
                        viewModel.resetPassword(email) { success, message ->
                            loading = false
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            if (success) {
                                // Navigate back to login after successful reset
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context.startActivity(intent)
                                activity?.finish()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Service unavailable", Toast.LENGTH_SHORT).show()
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
                shape = RoundedCornerShape(32.dp),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("SEND RESET LINK")
                }
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
                    .clickable(enabled = !loading) {
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        context.startActivity(intent)
                        activity?.finish()
                    },
                textAlign = TextAlign.Center
            )
        }
    }
}