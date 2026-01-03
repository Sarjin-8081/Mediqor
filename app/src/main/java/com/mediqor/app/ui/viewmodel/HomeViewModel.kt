package com.mediqor.app.viewmodel

import androidx.lifecycle.ViewModel
import com.mediqor.app.model.CategoryModel
import com.mediqor.app.model.ProductModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categories: StateFlow<List<CategoryModel>> = _categories.asStateFlow()

    private val _featuredProducts = MutableStateFlow<List<ProductModel>>(emptyList())
    val featuredProducts: StateFlow<List<ProductModel>> = _featuredProducts.asStateFlow()

    init {
        loadCategories()
        loadFeaturedProducts()
    }

    private fun loadCategories() {
        _categories.value = listOf(
            CategoryModel(
                id = "1",
                name = "Pharmacy",
                iconRes = android.R.drawable.ic_menu_add,
                route = "pharmacy",
                description = "Medicines and Health Products"
            ),
            CategoryModel(
                id = "2",
                name = "Family Care",
                iconRes = android.R.drawable.ic_menu_agenda,
                route = "familyCare",
                description = "Baby and Family Products"
            ),
            CategoryModel(
                id = "3",
                name = "Personal Care",
                iconRes = android.R.drawable.ic_menu_camera,
                route = "personalCare",
                description = "Skincare and Beauty"
            ),
            CategoryModel(
                id = "4",
                name = "Supplements",
                iconRes = android.R.drawable.ic_menu_gallery,
                route = "supplements",
                description = "Vitamins and Supplements"
            ),
            CategoryModel(
                id = "5",
                name = "Surgical",
                iconRes = android.R.drawable.ic_menu_help,
                route = "surgical",
                description = "Surgical Supplies"
            ),
            CategoryModel(
                id = "6",
                name = "Devices",
                iconRes = android.R.drawable.ic_menu_info_details,
                route = "devices",
                description = "Medical Devices"
            )
        )
    }

    private fun loadFeaturedProducts() {
        _featuredProducts.value = listOf(
            ProductModel(
                id = "1",
                name = "Zandu Balm Ultra Power-8ml",
                price = 120.0,
                description = "Pain relief balm",
                imageUrl = "",
                category = "Pharmacy"
            ),
            ProductModel(
                id = "2",
                name = "Vicks Vaporub",
                price = 85.0,
                description = "Cold and cough relief",
                imageUrl = "",
                category = "Pharmacy"
            ),
            ProductModel(
                id = "3",
                name = "The Derma Co 10% Niacinamide Serum - 30ml",
                price = 599.0,
                description = "Face serum for glowing skin",
                imageUrl = "",
                category = "Personal Care"
            ),
            ProductModel(
                id = "4",
                name = "Minimalist 02% Alpha Arbutin Face Serum | 30 ml",
                price = 699.0,
                description = "Brightening face serum",
                imageUrl = "",
                category = "Personal Care"
            )
        )
    }

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            loadFeaturedProducts()
        } else {
            val filtered = _featuredProducts.value.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            _featuredProducts.value = filtered
        }
    }
}