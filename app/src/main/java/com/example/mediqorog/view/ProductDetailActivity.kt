package com.example.mediqorog.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.model.ProductRatingSummary
import com.example.mediqorog.model.ReviewModel
import com.example.mediqorog.repository.CartRepositoryImpl
import com.example.mediqorog.repository.ReviewRepositoryImpl
import com.example.mediqorog.ui.screens.ProductDetailScreen
import com.example.mediqorog.viewmodel.CartViewModel
import com.example.mediqorog.viewmodel.CartViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Activity to show product details with review functionality
 * Opens when user clicks on a product from any category
 */
class ProductDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get product data from Intent
        val productId = intent.getStringExtra("PRODUCT_ID") ?: ""
        val productName = intent.getStringExtra("PRODUCT_NAME") ?: ""
        val productPrice = intent.getDoubleExtra("PRODUCT_PRICE", 0.0)
        val productImage = intent.getStringExtra("PRODUCT_IMAGE") ?: ""
        val productDescription = intent.getStringExtra("PRODUCT_DESCRIPTION") ?: ""
        val productCategory = intent.getStringExtra("PRODUCT_CATEGORY") ?: ""
        val productStock = intent.getIntExtra("PRODUCT_STOCK", 0)

        val product = ProductModel(
            id = productId,
            name = productName,
            price = productPrice,
            imageUrl = productImage,
            description = productDescription,
            category = productCategory,
            stock = productStock
        )

        setContent {
            ProductDetailActivityContent(
                product = product,
                onBackClick = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailActivityContent(
    product: ProductModel,
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val currentUserName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Anonymous"
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

    // Initialize repositories and viewmodels
    val cartRepo = CartRepositoryImpl()
    val reviewRepo = ReviewRepositoryImpl()
    val cartViewModel: CartViewModel = viewModel(
        factory = CartViewModelFactory(cartRepo)
    )

    // State for reviews
    var reviews by remember { mutableStateOf<List<ReviewModel>>(emptyList()) }
    var ratingSummary by remember { mutableStateOf(ProductRatingSummary()) }
    var isLoadingReviews by remember { mutableStateOf(true) }

    // Mock addresses - replace with actual address data from user profile
    val userAddresses = listOf(
        "Bagmati, Kathmandu Metro, 22 - Newroad Area, Newroad",
        "Home: Lalitpur, Patan, Near Patan Durbar Square",
        "Office: Kathmandu, Thamel, Ward 26"
    )

    // Function to load reviews
    fun loadReviews() {
        CoroutineScope(Dispatchers.Main).launch {
            isLoadingReviews = true

            // Get reviews
            reviewRepo.getProductReviews(product.id).fold(
                onSuccess = { reviews = it },
                onFailure = {
                    Toast.makeText(context, "Failed to load reviews", Toast.LENGTH_SHORT).show()
                }
            )

            // Get rating summary
            reviewRepo.getRatingSummary(product.id).fold(
                onSuccess = { ratingSummary = it },
                onFailure = { }
            )

            isLoadingReviews = false
        }
    }

    // Load reviews on first composition
    LaunchedEffect(product.id) {
        loadReviews()
    }

    // Observe cart messages
    val cartUiState by cartViewModel.uiState.collectAsState()

    LaunchedEffect(cartUiState.successMessage) {
        cartUiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            cartViewModel.clearMessages()
        }
    }

    LaunchedEffect(cartUiState.error) {
        cartUiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            cartViewModel.clearMessages()
        }
    }

    ProductDetailScreen(
        product = product,
        reviews = reviews,
        ratingSummary = ratingSummary,
        userAddresses = userAddresses,
        onAddToCart = { quantity ->
            if (currentUserId.isNotEmpty()) {
                cartViewModel.addToCart(
                    userId = currentUserId,
                    productId = product.id,
                    productName = product.name,
                    productImage = product.imageUrl,
                    price = product.price,
                    quantity = quantity,
                    category = product.category,
                    stock = product.stock
                )
            } else {
                Toast.makeText(context, "Please login to add to cart", Toast.LENGTH_SHORT).show()
            }
        },
        onBuyNow = { quantity ->
            if (currentUserId.isNotEmpty()) {
                // Add to cart
                cartViewModel.addToCart(
                    userId = currentUserId,
                    productId = product.id,
                    productName = product.name,
                    productImage = product.imageUrl,
                    price = product.price,
                    quantity = quantity,
                    category = product.category,
                    stock = product.stock
                )
                Toast.makeText(context, "Added to cart! Opening cart...", Toast.LENGTH_SHORT).show()
                // You can open CartActivity here if needed
            } else {
                Toast.makeText(context, "Please login to continue", Toast.LENGTH_SHORT).show()
            }
        },
        onBackClick = onBackClick,
        onSubmitReview = { rating, comment ->
            if (currentUserId.isNotEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val review = ReviewModel(
                        productId = product.id,
                        userId = currentUserId,
                        userName = currentUserName.ifEmpty { currentUserEmail.substringBefore("@") },
                        rating = rating,
                        comment = comment,
                        createdAt = System.currentTimeMillis()
                    )

                    reviewRepo.addReview(review).fold(
                        onSuccess = {
                            Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                            // Reload reviews to show the new one
                            loadReviews()
                        },
                        onFailure = { error ->
                            Toast.makeText(
                                context,
                                "Failed to submit review: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            } else {
                Toast.makeText(context, "Please login to submit a review", Toast.LENGTH_SHORT).show()
            }
        }
    )
}