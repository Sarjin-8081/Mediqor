package com.example.mediqorog.model

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val category: String = "",
    val stock: Int = 0  // ← ADD THIS LINE
)