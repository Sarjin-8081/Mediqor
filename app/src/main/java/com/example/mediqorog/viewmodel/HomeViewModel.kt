package com.example.mediqorog.viewmodel

import com.example.mediqorog.R
import com.example.mediqorog.model.Category
import com.example.mediqorog.model.Product
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    val categories = listOf(
        Category("1", "Pharmacy", R.drawable.pharmacy, 0xFFE8F5E9),
        Category("2", "Family Care", R.drawable.family_care, 0xFFFFF3E0),
        Category("3", "Personal Care", R.drawable.personal_care, 0xFFE3F2FD),
        Category("4", "Supplements", R.drawable.supplements, 0xFFFCE4EC),
        Category("5", "Surgical", R.drawable.surgical, 0xFFEDE7F6),
        Category("6", "Devices", R.drawable.devices, 0xFFE0F2F1)
    )
    val products = mutableStateOf(
        listOf(
            Product(
                id = "1",
                name = "Paracetamol 500mg",
                description = "Pain relief tablets",
                price = 5.99,
                imageUrl = "https://example.com/paracetamol.jpg",
                rating = 4.5f,
                inStock = true,
                category = "Pharmacy"
            ),
            Product(
                id = "2",
                name = "Vitamin C 1000mg",
                description = "Immune support supplement",
                price = 12.99,
                imageUrl = "https://example.com/vitaminc.jpg",
                rating = 4.8f,
                inStock = true,
                category = "Supplements"
            ),
            Product(
                id = "3",
                name = "Hand Sanitizer 500ml",
                description = "Antibacterial hand sanitizer",
                price = 8.99,
                imageUrl = "https://example.com/sanitizer.jpg",
                rating = 4.3f,
                inStock = true,
                category = "Personal Care"
            ),
            Product(
                id = "4",
                name = "Digital Thermometer",
                description = "Fast and accurate temperature reading",
                price = 15.99,
                imageUrl = "https://example.com/thermometer.jpg",
                rating = 4.6f,
                inStock = true,
                category = "Devices"
            ),
            Product(
                id = "5",
                name = "First Aid Kit",
                description = "Complete emergency kit",
                price = 25.99,
                imageUrl = "https://example.com/firstaid.jpg",
                rating = 4.7f,
                inStock = true,
                category = "Family Care"
            ),
            Product(
                id = "6",
                name = "Surgical Mask Box",
                description = "50 pieces disposable masks",
                price = 9.99,
                imageUrl = "https://example.com/mask.jpg",
                rating = 4.4f,
                inStock = true,
                category = "Surgical"
            )
        )
    )
    fun addToCart(product: Product) {
    }
    fun toggleFavorite(productId: String) {
    }
}