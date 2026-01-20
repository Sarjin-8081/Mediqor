package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductModel

interface ProductRepository {
    fun getProductsByCategory(category: String): List<ProductModel>
}