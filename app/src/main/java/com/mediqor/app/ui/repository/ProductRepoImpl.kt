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
            .addOnFailureListener {
                callback(false, it.message ?: "Failed to add product")
            }
    }

    override fun getAllProducts(
        callback: (List<ProductModel>) -> Unit
    ) {
        productRef.get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(ProductModel::class.java)
                callback(products)
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
            .addOnFailureListener {
                callback(false, it.message ?: "Delete failed")
            }
    }
}
