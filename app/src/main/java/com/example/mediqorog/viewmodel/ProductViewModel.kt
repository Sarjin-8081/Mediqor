package com.example.mediqorog.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.CommonRepo
import com.example.mediqorog.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProductUiState {
    object Idle : ProductUiState()
    object Loading : ProductUiState()
    data class Success(val products: List<ProductModel>) : ProductUiState()
    data class Error(val message: String) : ProductUiState()
}

data class ProductOperationState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val commonRepo: CommonRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Idle)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _operationState = MutableStateFlow(ProductOperationState())
    val operationState: StateFlow<ProductOperationState> = _operationState.asStateFlow()

    private val _selectedProduct = MutableStateFlow<ProductModel?>(null)
    val selectedProduct: StateFlow<ProductModel?> = _selectedProduct.asStateFlow()

    fun loadAllProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading

            productRepository.getAllProducts()
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { exception ->
                    _uiState.value = ProductUiState.Error(
                        exception.message ?: "Failed to load products"
                    )
                }
        }
    }

    fun loadProductsByCategory(category: String) {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading

            productRepository.getProductsByCategory(category)
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { exception ->
                    _uiState.value = ProductUiState.Error(
                        exception.message ?: "Failed to load products"
                    )
                }
        }
    }

    fun loadFeaturedProducts() {
        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading

            productRepository.getFeaturedProducts()
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { exception ->
                    _uiState.value = ProductUiState.Error(
                        exception.message ?: "Failed to load featured products"
                    )
                }
        }
    }

    fun loadProductById(productId: String) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true)

            productRepository.getProductById(productId)
                .onSuccess { product ->
                    _selectedProduct.value = product
                    _operationState.value = ProductOperationState(isLoading = false)
                }
                .onFailure { exception ->
                    _operationState.value = ProductOperationState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load product"
                    )
                }
        }
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            loadAllProducts()
            return
        }

        viewModelScope.launch {
            _uiState.value = ProductUiState.Loading

            productRepository.searchProducts(query)
                .onSuccess { products ->
                    _uiState.value = ProductUiState.Success(products)
                }
                .onFailure { exception ->
                    _uiState.value = ProductUiState.Error(
                        exception.message ?: "Failed to search products"
                    )
                }
        }
    }

    fun addProduct(
        context: Context,
        imageUri: Uri?,
        product: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true)

            if (imageUri != null) {
                uploadProductImage(context, imageUri) { imageUrl, publicId ->
                    // Launch a new coroutine for the callback
                    viewModelScope.launch {
                        if (imageUrl != null && publicId != null) {
                            val productWithImage = product.copy(
                                imageUrl = imageUrl,
                                imagePublicId = publicId
                            )
                            addProductToFirestore(productWithImage, callback)
                        } else {
                            _operationState.value = ProductOperationState(
                                isLoading = false,
                                errorMessage = "Failed to upload image"
                            )
                            callback(false, "Failed to upload image")
                        }
                    }
                }
            } else {
                addProductToFirestore(product, callback)
            }
        }
    }

    private suspend fun addProductToFirestore(
        product: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        productRepository.addProduct(product)
            .onSuccess { productId ->
                _operationState.value = ProductOperationState(
                    isLoading = false,
                    successMessage = "Product added successfully"
                )
                callback(true, productId)
                loadAllProducts()
            }
            .onFailure { exception ->
                _operationState.value = ProductOperationState(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to add product"
                )
                callback(false, exception.message ?: "Failed to add product")
            }
    }

    fun updateProduct(
        context: Context,
        newImageUri: Uri?,
        product: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true)

            if (newImageUri != null) {
                if (product.imagePublicId.isNotEmpty()) {
                    deleteProductImage(product.imagePublicId) { _, _ -> }
                }

                uploadProductImage(context, newImageUri) { imageUrl, publicId ->
                    // Launch a new coroutine for the callback
                    viewModelScope.launch {
                        if (imageUrl != null && publicId != null) {
                            val updatedProduct = product.copy(
                                imageUrl = imageUrl,
                                imagePublicId = publicId,
                                updatedAt = System.currentTimeMillis()
                            )
                            updateProductInFirestore(updatedProduct, callback)
                        } else {
                            _operationState.value = ProductOperationState(
                                isLoading = false,
                                errorMessage = "Failed to upload new image"
                            )
                            callback(false, "Failed to upload new image")
                        }
                    }
                }
            } else {
                val updatedProduct = product.copy(updatedAt = System.currentTimeMillis())
                updateProductInFirestore(updatedProduct, callback)
            }
        }
    }

    private suspend fun updateProductInFirestore(
        product: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        productRepository.updateProduct(product)
            .onSuccess {
                _operationState.value = ProductOperationState(
                    isLoading = false,
                    successMessage = "Product updated successfully"
                )
                callback(true, "Product updated successfully")
                loadAllProducts()
            }
            .onFailure { exception ->
                _operationState.value = ProductOperationState(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to update product"
                )
                callback(false, exception.message ?: "Failed to update product")
            }
    }

    fun deleteProduct(product: ProductModel, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true)

            productRepository.deleteProduct(product.id)
                .onSuccess {
                    if (product.imagePublicId.isNotEmpty()) {
                        deleteProductImage(product.imagePublicId) { _, _ -> }
                    }

                    _operationState.value = ProductOperationState(
                        isLoading = false,
                        successMessage = "Product deleted successfully"
                    )
                    callback(true, "Product deleted successfully")
                    loadAllProducts()
                }
                .onFailure { exception ->
                    _operationState.value = ProductOperationState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to delete product"
                    )
                    callback(false, exception.message ?: "Failed to delete product")
                }
        }
    }

    fun updateStock(productId: String, newStock: Int, callback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _operationState.value = _operationState.value.copy(isLoading = true)

            productRepository.updateStock(productId, newStock)
                .onSuccess {
                    _operationState.value = ProductOperationState(
                        isLoading = false,
                        successMessage = "Stock updated successfully"
                    )
                    callback(true, "Stock updated successfully")
                    loadAllProducts()
                }
                .onFailure { exception ->
                    _operationState.value = ProductOperationState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to update stock"
                    )
                    callback(false, exception.message ?: "Failed to update stock")
                }
        }
    }

    private fun uploadProductImage(
        context: Context,
        imageUri: Uri,
        callback: (String?, String?) -> Unit
    ) {
        commonRepo.uploadImage(
            context = context,
            imageUri = imageUri,
            folder = "products",
            callback = { success, message, imageUrl, publicId ->
                if (success && imageUrl != null && publicId != null) {
                    callback(imageUrl, publicId)
                } else {
                    callback(null, null)
                }
            }
        )
    }

    private fun deleteProductImage(publicId: String, callback: (Boolean, String) -> Unit) {
        commonRepo.deleteImage(publicId, callback)
    }

    fun clearOperationState() {
        _operationState.value = ProductOperationState()
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }
}