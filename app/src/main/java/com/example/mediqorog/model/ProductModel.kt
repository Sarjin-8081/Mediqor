// model/Product.kt
package com.example.mediqorog.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String = "",
    val description: String = "",
    val category: String,
    val discount: Int = 0,
    val inStock: Boolean = true
)