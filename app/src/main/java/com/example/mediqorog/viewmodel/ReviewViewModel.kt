package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.ProductRatingSummary
import com.example.mediqorog.model.ReviewModel
import com.example.mediqorog.repository.ReviewRepo
import com.example.mediqorog.repository.ReviewRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val reviewRepository: ReviewRepo = ReviewRepositoryImpl()
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<ReviewModel>>(emptyList())
    val reviews: StateFlow<List<ReviewModel>> = _reviews.asStateFlow()

    private val _ratingSummary = MutableStateFlow(
        ProductRatingSummary(
            averageRating = 0.0,
            totalRatings = 0,
            fiveStarCount = 0,
            fourStarCount = 0,
            threeStarCount = 0,
            twoStarCount = 0,
            oneStarCount = 0
        )
    )
    val ratingSummary: StateFlow<ProductRatingSummary> = _ratingSummary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProductReviews(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            reviewRepository.getProductReviews(productId)
                .onSuccess { reviewsList ->
                    _reviews.value = reviewsList
                    _isLoading.value = false
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    _isLoading.value = false
                }
        }
    }

    fun loadRatingSummary(productId: String) {
        viewModelScope.launch {
            reviewRepository.getRatingSummary(productId)
                .onSuccess { summary ->
                    _ratingSummary.value = summary
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
        }
    }

    suspend fun submitReview(
        productId: String,
        userId: String,
        rating: Int,
        comment: String,
        title: String = ""
    ): Result<String> {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName ?: "Anonymous"
        val userImage = currentUser?.photoUrl?.toString() ?: ""

        val review = ReviewModel(
            id = "",
            productId = productId,
            userId = userId,
            userName = userName,
            userImage = userImage,
            rating = rating,
            title = title,
            comment = comment,
            images = emptyList(),
            isVerifiedPurchase = false,
            likes = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        return reviewRepository.addReview(review)
    }

    fun clearError() {
        _error.value = null
    }
}