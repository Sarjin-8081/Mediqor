package com.example.mediqorog.model

data class ProductModel(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrl: String = "",
    val inStock: Boolean = true
)
 {
    fun getFormattedPrice(): String {
        return "Rs. %.2f".format(price)
    }
}