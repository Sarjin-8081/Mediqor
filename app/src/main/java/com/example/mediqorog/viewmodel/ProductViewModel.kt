package com.example.mediqorog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.repository.ProductRepository
import com.example.mediqorog.repository.ProductRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val repository: ProductRepository = ProductRepositoryImpl()
) : ViewModel() {

    // UI State for products list
    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Single product state (for detail screen)
    private val _selectedProduct = MutableStateFlow<ProductModel?>(null)
    val selectedProduct: StateFlow<ProductModel?> = _selectedProduct.asStateFlow()

    // Load all products
    fun loadAllProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getAllProducts()
                .onSuccess { productList ->
                    _products.value = productList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load products"
                }

            _isLoading.value = false
        }
    }

    // Load products by category
    fun loadProductsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getProductsByCategory(category)
                .onSuccess { productList ->
                    _products.value = productList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load products"
                }

            _isLoading.value = false
        }
    }

    // Load featured products
    fun loadFeaturedProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getFeaturedProducts()
                .onSuccess { productList ->
                    _products.value = productList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load featured products"
                }

            _isLoading.value = false
        }
    }

    // Search products
    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.searchProducts(query)
                .onSuccess { productList ->
                    _products.value = productList
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Search failed"
                }

            _isLoading.value = false
        }
    }

    // Get single product by ID
    fun getProductById(productId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getProductById(productId)
                .onSuccess { product ->
                    _selectedProduct.value = product
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to load product"
                }

            _isLoading.value = false
        }
    }

    // Add new product
    fun addProduct(product: ProductModel, onSuccess: (String) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.addProduct(product)
                .onSuccess { productId ->
                    onSuccess(productId)
                    // Reload products after adding
                    loadAllProducts()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to add product"
                }

            _isLoading.value = false
        }
    }

    // Update product
    fun updateProduct(product: ProductModel, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.updateProduct(product)
                .onSuccess {
                    onSuccess()
                    // Reload products after updating
                    loadAllProducts()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to update product"
                }

            _isLoading.value = false
        }
    }

    // Delete product
    fun deleteProduct(productId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.deleteProduct(productId)
                .onSuccess {
                    onSuccess()
                    // Reload products after deleting
                    loadAllProducts()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to delete product"
                }

            _isLoading.value = false
        }
    }

    // Update stock
    fun updateStock(productId: String, newStock: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.updateStock(productId, newStock)
                .onSuccess {
                    onSuccess()
                    // Reload products after stock update
                    loadAllProducts()
                }
                .onFailure { exception ->
                    _error.value = exception.message ?: "Failed to update stock"
                }

            _isLoading.value = false
        }
    }

    // Clear error
    fun clearError() {
        _error.value = null
    }
}