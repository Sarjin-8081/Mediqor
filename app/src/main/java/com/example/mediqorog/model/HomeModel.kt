package com.example.mediqorog.model

data class Category(
    val id: String,
    val title: String,
    val icon: Int,
    val backgroundColor: Long
)
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val rating: Float = 0f,
    val inStock: Boolean = true,
    val category: String = ""
)