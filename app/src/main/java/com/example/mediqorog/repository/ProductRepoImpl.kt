// repository/implement/ProductRepositoryImpl.kt
package com.example.mediqorog.repository.implement

import com.example.mediqorog.model.Product
import com.example.mediqorog.repository.ProductRepository

class ProductRepositoryImpl : ProductRepository {

    override fun getProductsByCategory(category: String): List<Product> {
        return when(category.lowercase()) {
            "pharmacy" -> getPharmacyProducts()
            "family care" -> getFamilyCareProducts()
            "personal care" -> getPersonalCareProducts()
            "supplements" -> getSupplementsProducts()
            "surgical" -> getSurgicalProducts()
            "devices" -> getDevicesProducts()
            else -> emptyList()
        }
    }

    private fun getPharmacyProducts(): List<Product> {
        return listOf(
            Product(
                id = "1",
                name = "Paracetamol 500mg",
                price = 50.0,
                description = "Pain Relief",
                category = "pharmacy",
                discount = 10
            ),
            Product(
                id = "2",
                name = "Vitamin C 1000mg",
                price = 120.0,
                description = "Immunity Booster",
                category = "pharmacy",
                discount = 15
            ),
            Product(
                id = "3",
                name = "Hand Sanitizer 500ml",
                price = 80.0,
                description = "Hygiene",
                category = "pharmacy",
                discount = 5
            ),
            Product(
                id = "4",
                name = "Cough Syrup",
                price = 150.0,
                description = "Cold & Flu",
                category = "pharmacy"
            ),
            Product(
                id = "5",
                name = "Bandage Roll",
                price = 45.0,
                description = "First Aid",
                category = "pharmacy"
            ),
            Product(
                id = "6",
                name = "Antiseptic Cream",
                price = 95.0,
                description = "Wound Care",
                category = "pharmacy"
            )
        )
    }

    private fun getFamilyCareProducts(): List<Product> {
        return listOf(
            Product(
                id = "7",
                name = "Baby Diapers",
                price = 450.0,
                description = "Baby Care",
                category = "family care",
                discount = 20
            ),
            Product(
                id = "8",
                name = "Baby Wipes",
                price = 180.0,
                description = "Baby Hygiene",
                category = "family care"
            ),
            Product(
                id = "9",
                name = "Family Health Kit",
                price = 890.0,
                description = "Complete Kit",
                category = "family care",
                discount = 10
            ),
            Product(
                id = "10",
                name = "Baby Shampoo",
                price = 220.0,
                description = "Gentle Care",
                category = "family care"
            ),
            Product(
                id = "11",
                name = "Baby Powder",
                price = 150.0,
                description = "Soft Skin",
                category = "family care"
            ),
            Product(
                id = "12",
                name = "Feeding Bottle",
                price = 320.0,
                description = "Baby Feeding",
                category = "family care"
            )
        )
    }

    private fun getPersonalCareProducts(): List<Product> {
        return listOf(
            Product(
                id = "13",
                name = "Face Wash",
                price = 250.0,
                description = "Skincare",
                category = "personal care",
                discount = 15
            ),
            Product(
                id = "14",
                name = "Body Lotion",
                price = 180.0,
                description = "Moisturizer",
                category = "personal care",
                discount = 10
            ),
            Product(
                id = "15",
                name = "Shampoo",
                price = 280.0,
                description = "Hair Care",
                category = "personal care"
            ),
            Product(
                id = "16",
                name = "Conditioner",
                price = 320.0,
                description = "Hair Care",
                category = "personal care"
            ),
            Product(
                id = "17",
                name = "Sunscreen SPF 50",
                price = 450.0,
                description = "Sun Protection",
                category = "personal care"
            ),
            Product(
                id = "18",
                name = "Toothpaste",
                price = 120.0,
                description = "Dental Care",
                category = "personal care"
            )
        )
    }

    private fun getSupplementsProducts(): List<Product> {
        return listOf(
            Product(
                id = "19",
                name = "Multivitamin Tablets",
                price = 550.0,
                description = "Daily Vitamins",
                category = "supplements",
                discount = 12
            ),
            Product(
                id = "20",
                name = "Omega-3 Capsules",
                price = 780.0,
                description = "Heart Health",
                category = "supplements",
                discount = 18
            ),
            Product(
                id = "21",
                name = "Protein Powder",
                price = 1200.0,
                description = "Muscle Building",
                category = "supplements"
            ),
            Product(
                id = "22",
                name = "Calcium Tablets",
                price = 420.0,
                description = "Bone Health",
                category = "supplements"
            ),
            Product(
                id = "23",
                name = "Iron Supplements",
                price = 380.0,
                description = "Energy Boost",
                category = "supplements"
            ),
            Product(
                id = "24",
                name = "Vitamin D3",
                price = 340.0,
                description = "Immunity",
                category = "supplements"
            )
        )
    }

    private fun getSurgicalProducts(): List<Product> {
        return listOf(
            Product(
                id = "25",
                name = "Surgical Mask (50pcs)",
                price = 350.0,
                description = "PPE",
                category = "surgical",
                discount = 8
            ),
            Product(
                id = "26",
                name = "Surgical Gloves",
                price = 120.0,
                description = "Protection",
                category = "surgical",
                discount = 10
            ),
            Product(
                id = "27",
                name = "Surgical Scissors",
                price = 280.0,
                description = "Medical Tools",
                category = "surgical"
            ),
            Product(
                id = "28",
                name = "Gauze Pads",
                price = 95.0,
                description = "Wound Dressing",
                category = "surgical"
            ),
            Product(
                id = "29",
                name = "Medical Tape",
                price = 65.0,
                description = "Adhesive",
                category = "surgical"
            ),
            Product(
                id = "30",
                name = "Syringes (10pcs)",
                price = 150.0,
                description = "Medical Use",
                category = "surgical"
            )
        )
    }

    private fun getDevicesProducts(): List<Product> {
        return listOf(
            Product(
                id = "31",
                name = "Blood Pressure Monitor",
                price = 2500.0,
                description = "Digital Monitor",
                category = "devices",
                discount = 15
            ),
            Product(
                id = "32",
                name = "Digital Thermometer",
                price = 450.0,
                description = "Fast Reading",
                category = "devices",
                discount = 10
            ),
            Product(
                id = "33",
                name = "Glucose Meter",
                price = 1800.0,
                description = "Blood Sugar",
                category = "devices"
            ),
            Product(
                id = "34",
                name = "Pulse Oximeter",
                price = 1200.0,
                description = "Oxygen Level",
                category = "devices"
            ),
            Product(
                id = "35",
                name = "Nebulizer",
                price = 3500.0,
                description = "Respiratory Care",
                category = "devices"
            ),
            Product(
                id = "36",
                name = "Weighing Scale",
                price = 890.0,
                description = "Digital Scale",
                category = "devices"
            )
        )
    }
}