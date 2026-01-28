package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductRatingSummary
import com.example.mediqorog.model.ReviewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ReviewRepo {

    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun addReview(review: ReviewModel): Result<String> {
        return try {
            val reviewData = hashMapOf(
                "productId" to review.productId,
                "userId" to review.userId,
                "userName" to review.userName,
                "userImage" to review.userImage,
                "rating" to review.rating,
                "title" to review.title,
                "comment" to review.comment,
                "images" to review.images,
                "isVerifiedPurchase" to review.isVerifiedPurchase,
                "likes" to 0,
                "createdAt" to Timestamp.now(),
                "updatedAt" to Timestamp.now()
            )

            val docRef = reviewsCollection.add(reviewData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductReviews(productId: String): Result<List<ReviewModel>> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("productId", productId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { doc ->
                try {
                    ReviewModel(
                        id = doc.id,
                        productId = doc.getString("productId") ?: "",
                        userId = doc.getString("userId") ?: "",
                        userName = doc.getString("userName") ?: "Anonymous",
                        userImage = doc.getString("userImage") ?: "",
                        rating = doc.getLong("rating")?.toInt() ?: 0,
                        title = doc.getString("title") ?: "",
                        comment = doc.getString("comment") ?: "",
                        images = doc.get("images") as? List<String> ?: emptyList(),
                        isVerifiedPurchase = doc.getBoolean("isVerifiedPurchase") ?: false,
                        likes = doc.getLong("likes")?.toInt() ?: 0,
                        createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now(),
                        updatedAt = doc.getTimestamp("updatedAt") ?: Timestamp.now()
                    )
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRatingSummary(productId: String): Result<ProductRatingSummary> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("productId", productId)
                .get()
                .await()

            val reviews = snapshot.documents.mapNotNull { doc ->
                doc.getLong("rating")?.toInt()
            }

            val totalRatings = reviews.size
            val averageRating = if (totalRatings > 0) {
                reviews.sum().toDouble() / totalRatings
            } else 0.0

            val summary = ProductRatingSummary(
                averageRating = averageRating,
                totalRatings = totalRatings,
                fiveStarCount = reviews.count { it == 5 },
                fourStarCount = reviews.count { it == 4 },
                threeStarCount = reviews.count { it == 3 },
                twoStarCount = reviews.count { it == 2 },
                oneStarCount = reviews.count { it == 1 }
            )

            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserReview(productId: String, userId: String): Result<ReviewModel?> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("productId", productId)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            val review = snapshot.documents.firstOrNull()?.let { doc ->
                ReviewModel(
                    id = doc.id,
                    productId = doc.getString("productId") ?: "",
                    userId = doc.getString("userId") ?: "",
                    userName = doc.getString("userName") ?: "Anonymous",
                    userImage = doc.getString("userImage") ?: "",
                    rating = doc.getLong("rating")?.toInt() ?: 0,
                    title = doc.getString("title") ?: "",
                    comment = doc.getString("comment") ?: "",
                    images = doc.get("images") as? List<String> ?: emptyList(),
                    isVerifiedPurchase = doc.getBoolean("isVerifiedPurchase") ?: false,
                    likes = doc.getLong("likes")?.toInt() ?: 0,
                    createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now(),
                    updatedAt = doc.getTimestamp("updatedAt") ?: Timestamp.now()
                )
            }

            Result.success(review)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateReview(reviewId: String, review: ReviewModel): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).update(
                mapOf(
                    "rating" to review.rating,
                    "title" to review.title,
                    "comment" to review.comment,
                    "images" to review.images,
                    "updatedAt" to Timestamp.now()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}