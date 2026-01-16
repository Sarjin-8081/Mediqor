//package com.example.mediqorog.viewmodel
//
//import com.example.mediqorog.R
//import com.example.mediqorog.model.Category
//import com.example.mediqorog.model.Product
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//
//class HomeViewModel : ViewModel() {
//    val categories = listOf(
//        Category("1", "Pharmacy", R.drawable.pharmacy, 0xFFE8F5E9),
//        Category("2", "Family Care", R.drawable.family_care, 0xFFFFF3E0),
//        Category("3", "Personal Care", R.drawable.personal_care, 0xFFE3F2FD),
//        Category("4", "Supplements", R.drawable.supplements, 0xFFFCE4EC),
//        Category("5", "Surgical", R.drawable.surgical, 0xFFEDE7F6),
//        Category("6", "Devices", R.drawable.devices, 0xFFE0F2F1)
//    )
//    val products = mutableStateOf(
//        listOf(
//            Product(
//                id = "1",
//                name = "Paracetamol 500mg",
//                description = "Pain relief tablets",
//                price = 5.99,
//                imageUrl = "https://example.com/paracetamol.jpg",
//                rating = 4.5f,
//                inStock = true,
//                category = "Pharmacy"
//            ),
//            Product(
//                id = "2",
//                name = "Vitamin C 1000mg",
//                description = "Immune support supplement",
//                price = 12.99,
//                imageUrl = "https://example.com/vitaminc.jpg",
//                rating = 4.8f,
//                inStock = true,
//                category = "Supplements"
//            ),
//            Product(
//                id = "3",
//                name = "Hand Sanitizer 500ml",
//                description = "Antibacterial hand sanitizer",
//                price = 8.99,
//                imageUrl = "https://example.com/sanitizer.jpg",
//                rating = 4.3f,
//                inStock = true,
//                category = "Personal Care"
//            ),
//            Product(
//                id = "4",
//                name = "Digital Thermometer",
//                description = "Fast and accurate temperature reading",
//                price = 15.99,
//                imageUrl = "https://example.com/thermometer.jpg",
//                rating = 4.6f,
//                inStock = true,
//                category = "Devices"
//            ),
//            Product(
//                id = "5",
//                name = "First Aid Kit",
//                description = "Complete emergency kit",
//                price = 25.99,
//                imageUrl = "https://example.com/firstaid.jpg",
//                rating = 4.7f,
//                inStock = true,
//                category = "Family Care"
//            ),
//            Product(
//                id = "6",
//                name = "Surgical Mask Box",
//                description = "50 pieces disposable masks",
//                price = 9.99,
//                imageUrl = "https://example.com/mask.jpg",
//                rating = 4.4f,
//                inStock = true,
//                category = "Surgical"
//            )
//        )
//    )
//    fun addToCart(product: Product) {
//    }
//    fun toggleFavorite(productId: String) {
//    }
//}

package com.example.mediqorog.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.mediqorog.R
import com.example.mediqorog.model.CategoryModel
import com.example.mediqorog.model.ProductModel


class HomeViewModel : ViewModel() {

        val categories = listOf(
            CategoryModel(
                title = "Pharmacy",
                icon = Icons.Default.LocalPharmacy,
                color = Color(0xFF4CAF50),
                drawableRes = R.drawable.pharmacy
            ),
            CategoryModel(
                title = "Family Care",
                icon = Icons.Default.FamilyRestroom,
                color = Color(0xFF2196F3),
                drawableRes = R.drawable.family_care
            ),
            CategoryModel(
                title = "Personal Care",
                icon = Icons.Default.Person,
                color = Color(0xFFFF9800),
                drawableRes = R.drawable.personal_care
            ),
            CategoryModel(
                title = "Supplements",
                icon = Icons.Default.Lightbulb,
                color = Color(0xFF9C27B0),
                drawableRes = R.drawable.supplements
            ),
            CategoryModel(
                title = "Surgical",
                icon = Icons.Default.Build,
                color = Color(0xFFF44336),
                drawableRes = R.drawable.surgical
            ),
            CategoryModel(
                title = "Devices",
                icon = Icons.Default.DevicesOther,
                color = Color(0xFF00BCD4),
                drawableRes = R.drawable.devices
            )
        )





    // Flash Sale Products
    val flashSaleProducts = listOf(
        ProductModel("1", "Paracetamol 500mg", "Pain relief", 50.0, category = "Pharmacy"),
        ProductModel("2", "Vitamin C 1000mg", "Immunity booster", 120.0, category = "Supplements"),
        ProductModel("3", "Hand Sanitizer", "70% Alcohol", 150.0, category = "Personal Care"),
        ProductModel("4", "Face Mask N95", "Pack of 5", 200.0, category = "Surgical")
    )

    // Top Selling Products
    val topSellingProducts = listOf(
        ProductModel("5", "Aspirin 25mg", "Heart health", 80.0, category = "Pharmacy"),
        ProductModel("6", "Omega-3 Fish Oil", "Brain & heart", 450.0, category = "Supplements"),
        ProductModel("7", "Baby Diapers", "Pack of 20", 450.0, category = "Family Care"),
        ProductModel("8", "BP Monitor", "Digital", 2500.0, category = "Devices")
    )

    // Sunscreen Products
    val sunscreenProducts = listOf(
        ProductModel("9", "Neutrogena SPF 50+", "UVA/UVB protection", 850.0, category = "Personal Care"),
        ProductModel("10", "Nivea Sun Protect", "SPF 30", 650.0, category = "Personal Care"),
        ProductModel("11", "Cetaphil Sun SPF 50", "For sensitive skin", 950.0, category = "Personal Care"),
        ProductModel("12", "Lotus Herbals SPF 40", "Matte finish", 550.0, category = "Personal Care")
    )

    // Body Lotion Products
    val bodyLotionProducts = listOf(
        ProductModel("13", "Vaseline Body Lotion", "Deep moisture", 350.0, category = "Personal Care"),
        ProductModel("14", "Nivea Nourishing Lotion", "24h hydration", 450.0, category = "Personal Care"),
        ProductModel("15", "Cetaphil Moisturizer", "For dry skin", 750.0, category = "Personal Care"),
        ProductModel("16", "Himalaya Body Lotion", "Herbal formula", 280.0, category = "Personal Care")
    )

    val products = flashSaleProducts + topSellingProducts + sunscreenProducts + bodyLotionProducts
}