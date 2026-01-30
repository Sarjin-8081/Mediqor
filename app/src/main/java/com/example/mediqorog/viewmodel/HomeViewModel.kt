package com.example.mediqorog.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.R
import com.example.mediqorog.model.CategoryModel
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.ProductRepository
import com.example.mediqorog.repository.ProductRepositoryImpl
import com.example.mediqorog.repository.ReviewRepo
import com.example.mediqorog.repository.ReviewRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productRepository: ProductRepository = ProductRepositoryImpl(),
    private val reviewRepository: ReviewRepo = ReviewRepositoryImpl()  // ✅ FIXED: Use ReviewRepo interface
) : ViewModel() {

    // StateFlow for products loaded from Firebase
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val categories = listOf(
        CategoryModel(
            title = "Pharmacy",
            icon = Icons.Default.LocalPharmacy,
            color = Color(0xFF4CAF50),
            drawableRes = R.drawable.pharmacy
        ),
        CategoryModel(
            title = "Family Care",
            icon = Icons.Default.FamilyRestroom,
            color = Color(0xFF2196F3),
            drawableRes = R.drawable.family_care
        ),
        CategoryModel(
            title = "Personal Care",
            icon = Icons.Default.Person,
            color = Color(0xFFFF9800),
            drawableRes = R.drawable.personal_care
        ),
        CategoryModel(
            title = "Supplements",
            icon = Icons.Default.Lightbulb,
            color = Color(0xFF9C27B0),
            drawableRes = R.drawable.supplements
        ),
        CategoryModel(
            title = "Surgical",
            icon = Icons.Default.Build,
            color = Color(0xFFF44336),
            drawableRes = R.drawable.surgical
        ),
        CategoryModel(
            title = "Devices",
            icon = Icons.Default.DevicesOther,
            color = Color(0xFF00BCD4),
            drawableRes = R.drawable.devices
        )
    )

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = productRepository.getAllProducts()
            result.onSuccess { productList ->
                // Load rating summaries for all products
                val productsWithRatings = productList.map { product ->
                    loadRatingSummaryForProduct(product)
                }
                _products.value = productsWithRatings
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load products"
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadRatingSummaryForProduct(product: ProductModel): ProductModel {
        return try {
            // ✅ FIXED: Use getRatingSummary() (not getReviewSummary)
            val summary = reviewRepository.getRatingSummary(product.id)
            summary.onSuccess { ratingSummary ->
                // ✅ FIXED: Use totalRatings (not totalReviews)
                return product.copy(
                    rating = ratingSummary.averageRating,
                    reviewCount = ratingSummary.totalRatings
                )
            }.onFailure {
                // If failed to get rating, return product as is
                return product
            }
            product
        } catch (e: Exception) {
            // If any error, return product as is
            product
        }
    }

    fun refreshProducts() {
        loadProducts()
    }

    fun clearError() {
        _error.value = null
    }
}