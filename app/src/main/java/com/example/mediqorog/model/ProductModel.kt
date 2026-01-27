package com.example.mediqorog.model

data class ProductModel(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val imagePublicId: String = "", // For Cloudinary deletion
    val category: String = "",
    val stock: Int = 0,
    val inStock: Boolean = true,
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)