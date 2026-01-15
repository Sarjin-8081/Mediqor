package com.example.mediqorog.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryModel(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val color: Color
)