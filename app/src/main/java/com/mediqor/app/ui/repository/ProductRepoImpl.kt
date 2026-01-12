package com.mediqor.app.ui.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mediqor.app.model.ProductModel

class ProductRepoImpl : ProductRepo {

    private val firestore = FirebaseFirestore.getInstance()
    private val productRef = firestore.collection("products")

    override fun addProduct(
        product: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        val doc = productRef.document()
        val newProduct = product.copy(id = doc.id)

        doc.set(newProduct)
            .addOnSuccessListener {
                callback(true, "Product added successfully")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Failed to add product")
            }
    }

    override fun getAllProducts(
        callback: (List<ProductModel>) -> Unit
    ) {
        productRef.get()
            .addOnSuccessListener { snapshot ->
                try {
                    val products = snapshot.toObjects(ProductModel::class.java)
                    callback(products)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(emptyList())
            }
    }

    override fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    ) {
        productRef.document(productId)
            .delete()
            .addOnSuccessListener {
                callback(true, "Product deleted")
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message ?: "Delete failed")
            }
    }
}