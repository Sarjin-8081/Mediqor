package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductModel

interface ProductRepository {

    // Get all products
    suspend fun getAllProducts(): Result<List<ProductModel>>

    // Get products by category
    suspend fun getProductsByCategory(category: String): Result<List<ProductModel>>

    // Get featured products
    suspend fun getFeaturedProducts(): Result<List<ProductModel>>

    // Get single product by ID
    suspend fun getProductById(productId: String): Result<ProductModel?>

    // Search products by name or tags
    suspend fun searchProducts(query: String): Result<List<ProductModel>>

    // Add new product
    suspend fun addProduct(product: ProductModel): Result<String>

    // Update existing product
    suspend fun updateProduct(product: ProductModel): Result<Unit>

    // Delete product (soft delete - set isActive = false)
    suspend fun deleteProduct(productId: String): Result<Unit>

    // Update stock
    suspend fun updateStock(productId: String, newStock: Int): Result<Unit>
}