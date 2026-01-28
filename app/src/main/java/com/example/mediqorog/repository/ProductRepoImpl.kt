package com.example.mediqorog.repository

import android.util.Log
import com.example.mediqorog.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl : ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")
    private val TAG = "ProductRepository"

    override suspend fun getAllProducts(): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toProductModel()
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing product ${doc.id}", e)
                    null
                }
            }.sortedByDescending { it.createdAt } // Sort in memory instead

            Log.d(TAG, "Loaded ${products.size} products")
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting products", e)
            Result.failure(e)
        }
    }

    override suspend fun getProductsByCategory(category: String): Result<List<ProductModel>> {
        return try {
            // Get all active products first
            val snapshot = productsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()

            // Filter in memory to support multiple categories (comma-separated)
            val products = snapshot.documents.mapNotNull { it.toProductModel() }
                .filter { product ->
                    // Split categories by comma and check if any match the selected category
                    product.category.split(",").any { cat ->
                        cat.trim().equals(category, ignoreCase = true)
                    }
                }
                .sortedByDescending { it.createdAt }

            Log.d(TAG, "Loaded ${products.size} products in category: $category")
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting products by category", e)
            Result.failure(e)
        }
    }

    override suspend fun getFeaturedProducts(): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isActive", true)
                .whereEqualTo("isFeatured", true)
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { it.toProductModel() }
                .sortedByDescending { it.createdAt } // Sort in memory instead

            Log.d(TAG, "Loaded ${products.size} featured products")
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting featured products", e)
            Result.failure(e)
        }
    }

    override suspend fun getProductById(productId: String): Result<ProductModel?> {
        return try {
            val document = productsCollection.document(productId).get().await()

            if (document.exists()) {
                val product = document.toProductModel()
                Log.d(TAG, "Product loaded: ${product?.name}")
                Result.success(product)
            } else {
                Log.w(TAG, "Product not found: $productId")
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting product", e)
            Result.failure(e)
        }
    }

    override suspend fun searchProducts(query: String): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val products = snapshot.documents.mapNotNull { it.toProductModel() }
                .filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.description.contains(query, ignoreCase = true) ||
                            product.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                }

            Log.d(TAG, "Found ${products.size} products matching: $query")
            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error searching products", e)
            Result.failure(e)
        }
    }

    override suspend fun addProduct(product: ProductModel): Result<String> {
        return try {
            val productData = hashMapOf(
                "name" to product.name,
                "description" to product.description,
                "price" to product.price,
                "category" to product.category,
                "imageUrl" to product.imageUrl,
                "imagePublicId" to product.imagePublicId,
                "stock" to product.stock,
                "inStock" to product.inStock,
                "isFeatured" to product.isFeatured,
                "tags" to product.tags,
                "isActive" to true,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            val documentReference = productsCollection.add(productData).await()
            Log.d(TAG, "Product added with ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding product", e)
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: ProductModel): Result<Unit> {
        return try {
            val productData = hashMapOf(
                "name" to product.name,
                "description" to product.description,
                "price" to product.price,
                "category" to product.category,
                "imageUrl" to product.imageUrl,
                "imagePublicId" to product.imagePublicId,
                "stock" to product.stock,
                "inStock" to product.inStock,
                "isFeatured" to product.isFeatured,
                "tags" to product.tags,
                "isActive" to product.isActive,
                "updatedAt" to System.currentTimeMillis()
            )

            productsCollection.document(product.id).update(productData as Map<String, Any>).await()
            Log.d(TAG, "Product updated: ${product.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating product", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId)
                .update(
                    mapOf(
                        "isActive" to false,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()

            Log.d(TAG, "Product soft deleted: $productId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting product", e)
            Result.failure(e)
        }
    }

    override suspend fun hardDeleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Log.d(TAG, "Product hard deleted: $productId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error hard deleting product", e)
            Result.failure(e)
        }
    }

    override suspend fun updateStock(productId: String, newStock: Int): Result<Unit> {
        return try {
            productsCollection.document(productId)
                .update(
                    mapOf(
                        "stock" to newStock,
                        "inStock" to (newStock > 0),
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()

            Log.d(TAG, "Stock updated for product: $productId, new stock: $newStock")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating stock", e)
            Result.failure(e)
        }
    }

    private fun com.google.firebase.firestore.DocumentSnapshot.toProductModel(): ProductModel? {
        return try {
            ProductModel(
                id = this.id,
                name = getString("name") ?: "",
                price = getDouble("price") ?: 0.0,
                description = getString("description") ?: "",
                imageUrl = getString("imageUrl") ?: "",
                imagePublicId = getString("imagePublicId") ?: "",
                category = getString("category") ?: "",
                stock = getLong("stock")?.toInt() ?: 0,
                inStock = getBoolean("inStock") ?: true,
                isFeatured = getBoolean("isFeatured") ?: false,
                tags = get("tags") as? List<String> ?: emptyList(),
                isActive = getBoolean("isActive") ?: true,
                createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error converting document to ProductModel", e)
            null
        }
    }
}