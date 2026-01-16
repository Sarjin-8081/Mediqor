package com.example.mediqorog.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryModel(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val drawableRes: Int  // This is for your custom drawable images
)