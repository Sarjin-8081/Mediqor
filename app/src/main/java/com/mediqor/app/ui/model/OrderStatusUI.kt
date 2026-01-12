package com.mediqor.app.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class OrderStatusUI(
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val textColor: androidx.compose.ui.graphics.Color,
    val icon: ImageVector,
    val text: String
)
