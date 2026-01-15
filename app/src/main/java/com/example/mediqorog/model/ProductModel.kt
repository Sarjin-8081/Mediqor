package com.example.mediqorog.model

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val inStock: Boolean = true
)