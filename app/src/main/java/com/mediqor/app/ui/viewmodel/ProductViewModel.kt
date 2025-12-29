package com.mediqor.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.mediqor.app.model.ProductModel
import com.mediqor.app.ui.repository.ProductRepoImpl

class ProductViewModel(private val repo: ProductRepoImpl) : ViewModel() {

    fun addProduct(
        product: ProductModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addProduct(product, callback)
    }
}
