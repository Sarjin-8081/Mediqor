package com.mediqor.app.ui.viewmodel

import com.mediqor.app.model.ProductModel

fun addProduct(
    product: ProductModel,
    callback: (Boolean, String) -> Unit
) {
    repo.addProduct(product, callback)
}
