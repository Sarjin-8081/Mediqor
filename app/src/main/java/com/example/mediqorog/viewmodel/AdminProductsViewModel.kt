package com.example.mediqorog.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CommonRepo
import com.example.mediqorog.repository.ProductRepository
import com.example.mediqorog.repository.ProductRepositoryImpl
import com.example.mediqorog.repository.CommonRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<ProductModel> = emptyList(),
    val filteredProducts: List<ProductModel> = emptyList(),
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val error: String? = null,
    val successMessage: String? = null
)

class AdminProductsViewModel(
    private val repository: ProductRepository = ProductRepositoryImpl(),
    private val commonRepo: CommonRepo = CommonRepoImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private val TAG = "AdminProductsVM"

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.getAllProducts()
            result.onSuccess { products ->
                Log.d(TAG, "Products loaded: ${products.size}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products,
                    filteredProducts = products
                )
            }.onFailure { error ->
                Log.e(TAG, "Failed to load products", error)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to load products"
                )
            }
        }
    }

    fun searchProducts(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isEmpty()) {
            filterByCategory(_uiState.value.selectedCategory)
            return
        }

        viewModelScope.launch {
            val result = repository.searchProducts(query)
            result.onSuccess { products ->
                val filtered = if (_uiState.value.selectedCategory != "All") {
                    products.filter { product ->
                        product.category.split(",").any { cat ->
                            cat.trim().equals(_uiState.value.selectedCategory, ignoreCase = true)
                        }
                    }
                } else {
                    products
                }
                _uiState.value = _uiState.value.copy(filteredProducts = filtered)
                Log.d(TAG, "Search results: ${filtered.size}")
            }.onFailure { error ->
                Log.e(TAG, "Search failed", error)
                _uiState.value = _uiState.value.copy(error = error.message)
            }
        }
    }

    fun filterByCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)

        if (category == "All") {
            _uiState.value = _uiState.value.copy(
                filteredProducts = _uiState.value.products
            )
        } else {
            viewModelScope.launch {
                val result = repository.getProductsByCategory(category)
                result.onSuccess { products ->
                    _uiState.value = _uiState.value.copy(filteredProducts = products)
                    Log.d(TAG, "Category filter: ${products.size} products")
                }.onFailure { error ->
                    Log.e(TAG, "Filter failed", error)
                    _uiState.value = _uiState.value.copy(error = error.message)
                }
            }
        }
    }

    fun addProduct(context: Context, product: ProductModel, imageUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            if (imageUri != null) {
                // Upload image to Cloudinary first
                commonRepo.uploadImage(
                    context = context,
                    imageUri = imageUri,
                    folder = "products",
                    callback = { success, message, imageUrl, publicId ->
                        viewModelScope.launch {
                            if (success && imageUrl != null && publicId != null) {
                                // Add product with image URL
                                val productWithImage = product.copy(
                                    imageUrl = imageUrl,
                                    imagePublicId = publicId
                                )
                                addProductToFirestore(productWithImage)
                            } else {
                                Log.e(TAG, "Failed to upload image: $message")
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = "Failed to upload image: $message"
                                )
                            }
                        }
                    }
                )
            } else {
                // Add product without image
                addProductToFirestore(product)
            }
        }
    }

    private suspend fun addProductToFirestore(product: ProductModel) {
        val result = repository.addProduct(product)
        result.onSuccess { productId ->
            Log.d(TAG, "Product added: $productId")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                successMessage = "Product added successfully"
            )
            loadProducts()
        }.onFailure { error ->
            Log.e(TAG, "Failed to add product", error)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = error.message ?: "Failed to add product"
            )
        }
    }

    fun updateProduct(context: Context, product: ProductModel, imageUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            if (imageUri != null) {
                // Delete old image if exists
                if (product.imagePublicId.isNotEmpty()) {
                    commonRepo.deleteImage(product.imagePublicId) { success, message ->
                        Log.d(TAG, "Old image deletion: $message")
                    }
                }

                // Upload new image to Cloudinary
                commonRepo.uploadImage(
                    context = context,
                    imageUri = imageUri,
                    folder = "products",
                    callback = { success, message, imageUrl, publicId ->
                        viewModelScope.launch {
                            if (success && imageUrl != null && publicId != null) {
                                // Update product with new image URL
                                val updatedProduct = product.copy(
                                    imageUrl = imageUrl,
                                    imagePublicId = publicId,
                                    updatedAt = System.currentTimeMillis()
                                )
                                updateProductInFirestore(updatedProduct)
                            } else {
                                Log.e(TAG, "Failed to upload new image: $message")
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    error = "Failed to upload new image: $message"
                                )
                            }
                        }
                    }
                )
            } else {
                // Update product without changing image
                val updatedProduct = product.copy(updatedAt = System.currentTimeMillis())
                updateProductInFirestore(updatedProduct)
            }
        }
    }

    private suspend fun updateProductInFirestore(product: ProductModel) {
        val result = repository.updateProduct(product)
        result.onSuccess {
            Log.d(TAG, "Product updated: ${product.id}")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                successMessage = "Product updated successfully"
            )
            loadProducts()
        }.onFailure { error ->
            Log.e(TAG, "Failed to update product", error)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = error.message ?: "Failed to update product"
            )
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Get product to access image publicId
            val productResult = repository.getProductById(productId)
            productResult.onSuccess { product ->
                // Delete from Firestore
                val result = repository.deleteProduct(productId)
                result.onSuccess {
                    // Delete image from Cloudinary if exists
                    product?.let {
                        if (it.imagePublicId.isNotEmpty()) {
                            commonRepo.deleteImage(it.imagePublicId) { success, message ->
                                Log.d(TAG, "Image deletion: $message")
                            }
                        }
                    }

                    Log.d(TAG, "Product deleted: $productId")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Product deleted successfully"
                    )
                    loadProducts()
                }.onFailure { error ->
                    Log.e(TAG, "Failed to delete product", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to delete product"
                    )
                }
            }.onFailure { error ->
                Log.e(TAG, "Failed to get product", error)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Failed to delete product"
                )
            }
        }
    }

    fun updateStock(productId: String, newStock: Int) {
        viewModelScope.launch {
            val result = repository.updateStock(productId, newStock)
            result.onSuccess {
                Log.d(TAG, "Stock updated: $productId -> $newStock")
                loadProducts()
            }.onFailure { error ->
                Log.e(TAG, "Failed to update stock", error)
                _uiState.value = _uiState.value.copy(error = error.message)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }
}