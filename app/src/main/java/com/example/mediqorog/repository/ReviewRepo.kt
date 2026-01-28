package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductRatingSummary
import com.example.mediqorog.model.ReviewModel

interface ReviewRepo {
    suspend fun addReview(review: ReviewModel): Result<String>
    suspend fun getProductReviews(productId: String): Result<List<ReviewModel>>
    suspend fun getRatingSummary(productId: String): Result<ProductRatingSummary>
    suspend fun getUserReview(productId: String, userId: String): Result<ReviewModel?>
    suspend fun updateReview(reviewId: String, review: ReviewModel): Result<Unit>
    suspend fun deleteReview(reviewId: String): Result<Unit>
}