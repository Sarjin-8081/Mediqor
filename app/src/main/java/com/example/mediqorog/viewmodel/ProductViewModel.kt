package com.example.mediqorog.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CommonRepo
import com.example.mediqorog.repository.ProductRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepo: ProductRepo,
    private val commonRepo: CommonRepo
) : ViewModel() {

    // State flows for UI
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Loads products filtered by category
     */
    fun loadProductsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            getAllProducts { productList, success, message ->
                _isLoading.value = false
                if (success && productList != null) {
                    _products.value = productList.filter { it.category == category }
                } else {
                    _errorMessage.value = message
                    _products.value = emptyList()
                }
            }
        }
    }

    /**
     * Loads all products
     */
    fun loadAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            getAllProducts { productList, success, message ->
                _isLoading.value = false
                if (success && productList != null) {
                    _products.value = productList
                } else {
                    _errorMessage.value = message
                    _products.value = emptyList()
                }
            }
        }
    }

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

    /**
     * Clears error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}