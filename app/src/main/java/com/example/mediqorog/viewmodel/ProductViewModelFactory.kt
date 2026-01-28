package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediqorog.repository.CommonRepo
import com.example.mediqorog.repository.ProductRepository

class ProductViewModelFactory(
    private val productRepository: ProductRepository,
    private val commonRepo: CommonRepo
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(productRepository, commonRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}