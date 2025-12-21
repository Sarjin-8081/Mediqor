package com.mediqor.app.ui.screens.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mediqor.app.ui.screens.dashboard.ui.theme.MediQorTheme

class SupplementsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SupplementsUI() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplementsUI() {
    val context = LocalContext.current
    val activity = context as ComponentActivity


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Supplements") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text("Supplements Screen")
        }
    }
}
