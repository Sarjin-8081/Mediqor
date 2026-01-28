package com.example.mediqorog.model

import com.google.firebase.Timestamp

data class ReviewModel(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val rating: Int = 0, // 1-5 stars
    val title: String = "",
    val comment: String = "",
    val images: List<String> = emptyList(), // Review images
    val isVerifiedPurchase: Boolean = false,
    val likes: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

data class ProductRatingSummary(
    val averageRating: Double = 0.0,
    val totalRatings: Int = 0,
    val fiveStarCount: Int = 0,
    val fourStarCount: Int = 0,
    val threeStarCount: Int = 0,
    val twoStarCount: Int = 0,
    val oneStarCount: Int = 0
)