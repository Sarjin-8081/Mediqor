// repository/ProductRepository.kt
package com.example.mediqorog.repository

import com.example.mediqorog.model.Product

interface ProductRepository {
    fun getProductsByCategory(category: String): List<Product>
}