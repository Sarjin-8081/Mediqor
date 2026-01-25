package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductModel
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepoImpl : ProductRepo {

    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")

    override fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit) {
        val productData = hashMapOf(
            "name" to product.name,
            "description" to product.description,
            "price" to product.price,
            "category" to product.category,
            "imageUrl" to product.imageUrl,
            "inStock" to true,
            "createdAt" to System.currentTimeMillis()
        )

        productsCollection
            .add(productData)
            .addOnSuccessListener { documentReference ->
                Log.d("ProductRepo", "Product added with ID: ${documentReference.id}")
                callback(true, "✅ Product added successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("ProductRepo", "Error adding product", e)
                callback(false, "❌ Failed to add product: ${e.message}")
            }
    }

    override fun getAllProduct(callback: (List<ProductModel>?, Boolean, String) -> Unit) {
        productsCollection
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val products = documents.mapNotNull { doc ->
                    try {
                        ProductModel(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            price = doc.getDouble("price") ?: 0.0,
                            description = doc.getString("description") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            category = doc.getString("category") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("ProductRepo", "Error parsing product ${doc.id}", e)
                        null
                    }
                }
                Log.d("ProductRepo", "Loaded ${products.size} products")
                callback(products, true, "Products loaded successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ProductRepo", "Error getting products", e)
                callback(null, false, "Failed to load products: ${e.message}")
            }
    }

    override fun getProductById(
        productId: String,
        callback: (ProductModel?, Boolean, String) -> Unit
    ) {
        productsCollection.document(productId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    try {
                        val product = ProductModel(
                            id = document.id,
                            name = document.getString("name") ?: "",
                            price = document.getDouble("price") ?: 0.0,
                            description = document.getString("description") ?: "",
                            imageUrl = document.getString("imageUrl") ?: "",
                            category = document.getString("category") ?: ""
                        )
                        Log.d("ProductRepo", "Product loaded: ${product.name}")
                        callback(product, true, "Product loaded")
                    } catch (e: Exception) {
                        Log.e("ProductRepo", "Error parsing product", e)
                        callback(null, false, "Error parsing product: ${e.message}")
                    }
                } else {
                    Log.w("ProductRepo", "Product not found: $productId")
                    callback(null, false, "Product not found")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ProductRepo", "Error getting product", e)
                callback(null, false, "Error: ${e.message}")
            }
    }

    override fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit) {
        productsCollection.document(productId)
            .delete()
            .addOnSuccessListener {
                Log.d("ProductRepo", "Product deleted: $productId")
                callback(true, "✅ Product deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ProductRepo", "Error deleting product", e)
                callback(false, "❌ Failed to delete: ${e.message}")
            }
    }
}