package com.mediqor.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.mediqor.app.ui.navigation.AppNavGraph
import com.mediqor.app.ui.view.ui.theme.MediQorTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MediQorTheme {
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }


    }
}
