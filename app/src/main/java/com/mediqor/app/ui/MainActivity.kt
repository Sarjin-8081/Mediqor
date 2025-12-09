package com.mediqor.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mediqor.app.ui.navigation.NavGraph
import com.mediqor.app.ui.screens.theme.ThemeMediqor
import com.mediqor.app.data.UserRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // create repository here (simple)
        val userRepository = UserRepository()

        setContent {
            ThemeMediqor { // or your app theme composable
                NavGraph(userRepository = userRepository)
            }
        }
    }
}
