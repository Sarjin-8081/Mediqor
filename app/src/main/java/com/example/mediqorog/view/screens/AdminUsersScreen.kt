package com.example.mediqorog.view.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.components.*
import com.example.mediqorog.ui.components.EnhancedUserDetailDialog
import com.example.mediqorog.viewmodel.AdminUsersViewModel

@Composable
fun AdminUsersScreenContent() {
    val context = LocalContext.current
    val viewModel: AdminUsersViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Users Management",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B8FAC)
                )

                Spacer(modifier = Modifier.height(12.dp))

                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.searchUsers(it) },
                    placeholder = "Search users..."
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilterDropdown(
                    selectedValue = uiState.selectedRole,
                    options = listOf("All", "customer", "admin"),
                    onValueChange = { viewModel.filterByRole(it) },
                    label = "Role"
                )
            }
        }

        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.filteredUsers.isEmpty()) {
            EmptyState(
                icon = Icons.Default.People,
                message = "No users found"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.filteredUsers) { user ->
                    var showDialog by remember { mutableStateOf(false) }

                    // ✅ No conversion needed - using User directly!
                    UserCard(
                        user = user,
                        onClick = { showDialog = true }
                    )

                    if (showDialog) {
                        EnhancedUserDetailDialog(
                            user = user, // ✅ Full user with all fields
                            onDismiss = { showDialog = false },
                            onDeleteUser = {
                                viewModel.deleteUser(user.uid)
                                showDialog = false
                            },
                            onViewOrders = {
                                // TODO: Navigate to orders screen filtered by this user
                                Toast.makeText(
                                    context,
                                    "View orders for ${user.displayName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onSuspendAccount = {
                                if (user.accountStatus == "active") {
                                    viewModel.suspendUser(user.uid)
                                } else {
                                    viewModel.reactivateUser(user.uid)
                                }
                                showDialog = false
                            },
                            onViewFullProfile = {
                                // TODO: Navigate to full profile screen
                                Toast.makeText(
                                    context,
                                    "View full profile for ${user.displayName}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}