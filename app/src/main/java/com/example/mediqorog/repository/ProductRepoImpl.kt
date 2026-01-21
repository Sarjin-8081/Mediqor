package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl : ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")

    override suspend fun getAllProducts(): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val products = snapshot.toObjects(ProductModel::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductsByCategory(category: String): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("category", category)
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val products = snapshot.toObjects(ProductModel::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFeaturedProducts(): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isFeatured", true)
                .whereEqualTo("isActive", true)
                .limit(10)
                .get()
                .await()

            val products = snapshot.toObjects(ProductModel::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(productId: String): Result<ProductModel?> {
        return try {
            val snapshot = productsCollection
                .document(productId)
                .get()
                .await()

            val product = snapshot.toObject(ProductModel::class.java)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchProducts(query: String): Result<List<ProductModel>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isActive", true)
                .get()
                .await()

            // Firestore doesn't support full-text search, so filter in memory
            val allProducts = snapshot.toObjects(ProductModel::class.java)
            val filtered = allProducts.filter { product ->
                product.name.contains(query, ignoreCase = true) ||
                        product.description.contains(query, ignoreCase = true) ||
                        product.tags.any { it.contains(query, ignoreCase = true) }
            }

            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProduct(product: ProductModel): Result<String> {
        return try {
            val docRef = if (product.id.isEmpty()) {
                productsCollection.document()
            } else {
                productsCollection.document(product.id)
            }

            val productWithId = product.copy(id = docRef.id)
            docRef.set(productWithId).await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: ProductModel): Result<Unit> {
        return try {
            productsCollection
                .document(product.id)
                .set(product)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            // Soft delete - set isActive to false
            productsCollection
                .document(productId)
                .update("isActive", false)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateStock(productId: String, newStock: Int): Result<Unit> {
        return try {
            productsCollection
                .document(productId)
                .update("stock", newStock)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}