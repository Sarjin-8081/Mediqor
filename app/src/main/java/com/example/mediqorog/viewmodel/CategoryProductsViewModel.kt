//package com.example.mediqorog.viewmodel
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import com.example.mediqorog.model.ProductModel
//import com.example.mediqorog.repository.ProductRepository
//import com.example.mediqorog.repository.ProductRepoImpl
//
//class CategoryProductsViewModel : ViewModel() {
//
//    private val repository: ProductRepository = ProductRepoImpl()
//
//    private val _products = MutableLiveData<List<ProductModel>>()
//    val products: LiveData<List<ProductModel>> = _products
//
//    private val _isLoading = MutableLiveData<Boolean>()
//    val isLoading: LiveData<Boolean> = _isLoading
//
//    fun loadProducts(category: String) {
//        _isLoading.value = true
//
//        // Simulate network delay
//        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
//            _products.value = repository.getProductsByCategory(category)
//            _isLoading.value = false
//        }, 500)
//    }
//}