//// ============================================
//// FILE 1: SplashActivity.kt - FIXED VERSION
//// ============================================
//package com.example.mediqorog.view
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import com.airbnb.lottie.compose.LottieAnimation
//import com.airbnb.lottie.compose.LottieCompositionSpec
//import com.airbnb.lottie.compose.LottieConstants
//import com.airbnb.lottie.compose.animateLottieCompositionAsState
//import com.airbnb.lottie.compose.rememberLottieComposition
//import kotlinx.coroutines.delay
//import com.example.mediqorog.R
//
//class SplashActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            SplashBody()
//        }
//    }
//}
//
//@Composable
//fun SplashBody() {
//    val context = LocalContext.current
//    val activity = context as? Activity
//
//    LaunchedEffect(Unit) {
//        delay(3000)
//        context.startActivity(Intent(context, LoginActivity::class.java))
//        activity?.finish()
//    }
//
//    val composition by rememberLottieComposition(
//        LottieCompositionSpec.RawRes(R.raw.loading_dots)
//    )
//
//    val progress by animateLottieCompositionAsState(
//        composition = composition,
//        iterations = LottieConstants.IterateForever
//    )
//
//    Scaffold(
//        containerColor = Color.White
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.new_mediqor),
//                    contentDescription = "App Logo",
//                    modifier = Modifier.size(150.dp)
//                )
//
//                Spacer(modifier = Modifier.height(50.dp))
//
//                LottieAnimation(
//                    composition = composition,
//                    progress = { progress },
//                    modifier = Modifier.size(200.dp),
//                    contentScale = ContentScale.Fit
//                )
//            }
//        }
//    }
//}
//

package com.example.mediqorog.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediqorog.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000)
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        (context as? ComponentActivity)?.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.new_mediqor),
                contentDescription = "MediQorog Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = Color(0xFF0B8FAC),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🎵 MediQorog",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0B8FAC)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // TEMPORARY LOGOUT BUTTON
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    android.widget.Toast.makeText(
                        context,
                        "Logged out! Restart app to see login screen",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Sign Out (For Testing)")
            }
        }
    }
}
