package com.example.mediqorog.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediqorog.R
import com.example.mediqorog.repository.UserRepoImpl
import com.example.mediqorog.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : ComponentActivity() {

    private lateinit var viewModel: UserViewModel
    private lateinit var auth: FirebaseAuth
    private var isNavigating = false // ✅ Prevent double navigation

    companion object {
        private const val TAG = "LoginActivity"
        private const val PREFS_NAME = "MediqorPrefs"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.signInWithGoogle(account) { success, message, isAdmin ->
                    runOnUiThread {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            navigateToDashboard(isAdmin)
                        }
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(
                    this,
                    "Google sign-in failed: ${e.statusCode}",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Sign-in error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repo = UserRepoImpl()
        viewModel = UserViewModel(repo)
        auth = FirebaseAuth.getInstance()

        // ✅ Load saved email if Remember Me was checked
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val rememberMe = prefs.getBoolean(KEY_REMEMBER_ME, false)
        val savedEmail = prefs.getString(KEY_SAVED_EMAIL, "")

        setContent {
            LoginBody(
                viewModel = viewModel,
                savedEmail = if (rememberMe) savedEmail ?: "" else "",
                rememberMeChecked = rememberMe,
                onGoogleSignInClick = {
                    try {
                        val signInIntent = viewModel.getGoogleSignInClient(this).signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Failed to start Google Sign-In: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onRememberMeChanged = { email, checked ->
                    saveRememberMe(email, checked)
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null && !isNavigating) {
            Log.d(TAG, "User already logged in: ${currentUser.email}")
            isNavigating = true

            // ✅ Use coroutine to properly wait for admin check
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val isAdmin = checkIfUserIsAdminSuspend()
                    Log.d(TAG, "Admin check result: $isAdmin for user ${currentUser.email}")
                    navigateToDashboard(isAdmin)
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking admin status: ${e.message}", e)
                    isNavigating = false
                    // Default to customer dashboard on error
                    navigateToDashboard(false)
                }
            }
        }
    }

    /**
     * ✅ Suspend function that properly waits for Firestore query
     */
    private suspend fun checkIfUserIsAdminSuspend(): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val userDoc = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .get()
                .await()

            val role = userDoc.getString("role") ?: "customer"
            Log.d(TAG, "User role from Firestore: $role")

            role.equals("admin", ignoreCase = true)
        } catch (e: Exception) {
            Log.e(TAG, "Error in checkIfUserIsAdminSuspend: ${e.message}", e)
            false
        }
    }

    private fun navigateToDashboard(isAdmin: Boolean) {
        if (isNavigating && !isFinishing) {
            Log.d(TAG, "Navigating to dashboard - isAdmin: $isAdmin")

            val intent = if (isAdmin) {
                Toast.makeText(this, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                Intent(this, AdminDashboardActivity::class.java)
            } else {
                Intent(this, DashboardActivity::class.java)
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    /**
     * ✅ Save Remember Me preference
     */
    private fun saveRememberMe(email: String, checked: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean(KEY_REMEMBER_ME, checked)
            if (checked) {
                putString(KEY_SAVED_EMAIL, email)
            } else {
                remove(KEY_SAVED_EMAIL)
            }
            apply()
        }
        Log.d(TAG, "Remember Me saved: $checked, Email: ${if (checked) email else "cleared"}")
    }
}

@Composable
fun LoginBody(
    viewModel: UserViewModel? = null,
    savedEmail: String = "",
    rememberMeChecked: Boolean = false,
    onGoogleSignInClick: () -> Unit = {},
    onRememberMeChanged: (String, Boolean) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf(savedEmail) }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(rememberMeChecked) }
    var visibility by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.new_mediqor),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "LOGIN",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(15.dp),
                placeholder = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0B8FAC),
                    unfocusedBorderColor = Color(0xFFE0F0F5)
                ),
                enabled = !loading
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                trailingIcon = {
                    IconButton(onClick = { visibility = !visibility }) {
                        Icon(
                            painter = if (visibility) {
                                painterResource(R.drawable.baseline_visibility_off_24)
                            } else {
                                painterResource(R.drawable.baseline_visibility_24)
                            },
                            contentDescription = if (visibility) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (visibility) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                shape = RoundedCornerShape(15.dp),
                placeholder = { Text("Password") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF0B8FAC),
                    unfocusedBorderColor = Color(0xFFE0F0F5)
                ),
                enabled = !loading
            )

            Spacer(modifier = Modifier.height(15.dp))

            // ✅ Remember Me Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(enabled = !loading) {
                        rememberMe = !rememberMe
                        onRememberMeChanged(email, rememberMe)
                    }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = {
                            rememberMe = it
                            onRememberMeChanged(email, it)
                        },
                        enabled = !loading,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF0B8FAC),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(
                        text = "Remember me",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Forget Password?",
                    style = TextStyle(
                        color = Color(0xFF0B8FAC),
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.clickable {
                        if (!loading) {
                            val intent = Intent(context, ForgotPasswordActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    loading = true

                    // ✅ Save Remember Me preference before login
                    onRememberMeChanged(email, rememberMe)

                    viewModel?.signIn(email, password) { success, message, isAdmin ->
                        activity?.runOnUiThread {
                            loading = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                            if (success) {
                                if (isAdmin) {
                                    val intent = Intent(context, AdminDashboardActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    context.startActivity(intent)
                                    Toast.makeText(context, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val intent = Intent(context, DashboardActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    context.startActivity(intent)
                                }
                                activity?.finish()
                            }
                        }
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
                shape = RoundedCornerShape(32.dp),
                enabled = !loading
            ) {
                Text(if (loading) "Loading..." else "LOGIN")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(
                    text = "  OR  ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = {
                    if (!loading) {
                        onGoogleSignInClick()
                    }
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                enabled = !loading
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.googlelogo),
                        contentDescription = "Google",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Continue with Google", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = buildAnnotatedString {
                    append("Don't have an account? ")
                    withStyle(SpanStyle(color = Color(0xFF0B8FAC), fontWeight = FontWeight.Bold)) {
                        append("Sign up")
                    }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp)
                    .clickable {
                        if (!loading) {
                            val intent = Intent(context, RegistrationActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
            )
        }
    }
}