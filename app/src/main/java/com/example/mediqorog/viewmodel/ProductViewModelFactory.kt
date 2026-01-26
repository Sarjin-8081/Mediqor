package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mediqorog.repository.CommonRepoImpl
import com.example.mediqorog.repository.ProductRepoImpl

class ProductViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(ProductRepoImpl(), CommonRepoImpl()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}