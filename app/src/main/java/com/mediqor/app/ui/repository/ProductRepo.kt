package com.mediqor.app.ui.repository

import com.mediqor.app.model.ProductModel

interface ProductRepo {

    fun addProduct(
        product: ProductModel,
        callback: (Boolean, String) -> Unit
    )

    fun getAllProducts(
        callback: (List<ProductModel>) -> Unit
    )

    fun deleteProduct(
        productId: String,
        callback: (Boolean, String) -> Unit
    )
}
