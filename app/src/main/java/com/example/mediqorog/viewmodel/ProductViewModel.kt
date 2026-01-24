package com.example.mediqorog.viewmodel

import android.content.Context
import android.net.Uri
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CommonRepo
import com.example.mediqorog.repository.ProductRepo

class ProductViewModel(
    private val productRepo: ProductRepo,
    private val commonRepo: CommonRepo
) {

    /**
     * Uploads product image to Cloudinary
     * Simplifies the CommonRepo callback for UI usage
     */
    fun uploadProductImage(
        context: Context,
        imageUri: Uri,
        callback: (String?) -> Unit
    ) {
        commonRepo.uploadImage(
            context = context,
            imageUri = imageUri,
            folder = "products",
            callback = { success, message, imageUrl, publicId ->
                if (success && imageUrl != null) {
                    callback(imageUrl)
                } else {
                    callback(null)
                }
            }
        )
    }

    /**
     * Adds a new product to Firestore
     */
    fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit) {
        productRepo.addProduct(product, callback)
    }

    /**
     * Gets all products from Firestore
     */
    fun getAllProducts(callback: (List<ProductModel>?, Boolean, String) -> Unit) {
        productRepo.getAllProduct(callback)
    }

    /**
     * Gets a single product by ID
     */
    fun getProductById(productId: String, callback: (ProductModel?, Boolean, String) -> Unit) {
        productRepo.getProductById(productId, callback)
    }

    /**
     * Deletes a product from Firestore
     */
    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit) {
        productRepo.deleteProduct(productId, callback)
    }

    /**
     * Deletes an image from Cloudinary
     * Useful when deleting a product or updating its image
     */
    fun deleteProductImage(publicId: String, callback: (Boolean, String) -> Unit) {
        commonRepo.deleteImage(publicId, callback)
    }
}