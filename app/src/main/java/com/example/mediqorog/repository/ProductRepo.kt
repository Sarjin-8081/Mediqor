package com.example.mediqorog.repository

import com.example.mediqorog.model.ProductModel

interface ProductRepo {

    /**
     * Adds a new product to Firestore
     */
    fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit)

    /**
     * Gets all products from Firestore
     */
    fun getAllProduct(callback: (List<ProductModel>?, Boolean, String) -> Unit)

    /**
     * Gets a single product by ID
     */
    fun getProductById(productId: String, callback: (ProductModel?, Boolean, String) -> Unit)

    /**
     * Deletes a product from Firestore
     */
    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit)
}