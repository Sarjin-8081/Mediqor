package com.mediqor.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mediqor.app.data.UserRepository
import com.mediqor.app.ui.screens.dashboard.DashboardScreen
import com.mediqor.app.ui.screens.auth.LoginActivity // not used as activity, but you'll have a login composable later
import com.mediqor.app.ui.screens.profile.ProfileScreen
import com.mediqor.app.ui.screens.profile.UserViewModel

@Composable
fun NavGraph(userRepository: UserRepository, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    // Decide initial destination based on auth state
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        "dashboard"
    } else {
        "auth" // route to auth stack; for now will navigate to dashboard after login or you can create auth screens inside nav
    }

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable("dashboard") {
            // Create a VM and pass repository
            val userViewModel: UserViewModel = viewModel(factory = UserViewModel.provideFactory(userRepository))
            DashboardScreen(navController = navController, userViewModel = userViewModel)
        }

        // Very simple auth placeholder — replace with full auth screens later
        composable("auth") {
            // If you already have LoginActivity as an activity, you might start it, or convert it to composable.
            // For now navigate to dashboard if user becomes signed in.
            // For simplicity, show a blank screen here or convert your existing LoginActivity to a composable and include it.
            // I'll start as a simple placeholder composable:
            androidx.compose.material3.Text("Please log in - convert your LoginActivity to composable or start Activity")
        }

        // You can add more top-level routes if needed:
        composable("profile_detail") {
            val userViewModel: UserViewModel = viewModel(factory = UserViewModel.provideFactory(userRepository))
            ProfileScreen(userViewModel = userViewModel, navController = navController)
        }
    }
}
