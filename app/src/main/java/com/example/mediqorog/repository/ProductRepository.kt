package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductModel

interface ProductRepository {
    suspend fun getAllProducts(): Result<List<ProductModel>>
    suspend fun getProductsByCategory(category: String): Result<List<ProductModel>>
    suspend fun getFeaturedProducts(): Result<List<ProductModel>>
    suspend fun getProductById(productId: String): Result<ProductModel?>
    suspend fun searchProducts(query: String): Result<List<ProductModel>>
    suspend fun addProduct(product: ProductModel): Result<String>
    suspend fun updateProduct(product: ProductModel): Result<Unit>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun hardDeleteProduct(productId: String): Result<Unit>
    suspend fun updateStock(productId: String, newStock: Int): Result<Unit>
}