package com.mediqor.app.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.R


private val Unit.drawable: Any
    get() {
        TODO()
    }

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = try { context as Activity } catch (e: Exception) { null }

    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val localEmail: String? = sharedPreferences.getString("email","")
    val localPassword: String? = sharedPreferences.getString("password","")

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .background(Color.White)
        ) {
            // Top-right logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_home_24),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                "LOGIN",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Field
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

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                trailingIcon = {
                    IconButton(onClick = { visibility = !visibility }) {
                        Icon(
                            painter = if (visibility)
                                painterResource(R.drawable.baseline_visibility_off_24)
                            else
                                painterResource(R.drawable.baseline_visibility_24),
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(15.dp),
                placeholder = { Text("Password") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF0B8FAC),
                    unfocusedIndicatorColor = Color(0xFFE0F0F5)
                )
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                "Forget Password",
                style = TextStyle(
                    color = Color(0xFF0B8FAC),
                    textAlign = TextAlign.End
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 15.dp)
                    .clickable {
                        val intent = Intent(context, ForgotPasswordActivity::class.java)
                        context.startActivity(intent)
                    }
            )

            // Login Button
            Button(
                onClick = {
                    if (localEmail == email && localPassword == password) {
                        val intent = Intent(context, OnboardingActivity::class.java)
                        context.startActivity(intent)
                        activity?.finish()
                    } else {
                        Toast.makeText(context, "Invalid details", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(60.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 15.dp),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text("LOGIN")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sign up redirect
            Text(
                buildAnnotatedString {
                    append("Don't have an account? ")
                    withStyle(SpanStyle(color = Color(0xFF0B8FAC), fontWeight = FontWeight.Bold)) {
                        append("Sign up")
                    }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .clickable{
                        val intent = Intent(context, RegistrationActivity::class.java)
                        context.startActivity(intent)
                    }
            )
        }
    }
}

@Preview
@Composable
fun PreviewLogin() {
    LoginBody()
}