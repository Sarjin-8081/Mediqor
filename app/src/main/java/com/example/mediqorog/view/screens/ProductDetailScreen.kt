package com.example.mediqorog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.model.ProductRatingSummary
import com.example.mediqorog.model.ReviewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductModel,
    reviews: List<ReviewModel>,
    ratingSummary: ProductRatingSummary,
    userAddresses: List<String>, // Delivery addresses
    onAddToCart: (quantity: Int) -> Unit,
    onBuyNow: (quantity: Int) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableStateOf(1) }
    var selectedTab by remember { mutableStateOf(0) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var selectedAddress by remember { mutableStateOf(userAddresses.firstOrNull() ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, "Share")
                    }
                    IconButton(onClick = { /* Favorite */ }) {
                        Icon(Icons.Default.FavoriteBorder, "Favorite")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B8FAC),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomCartBar(
                price = product.price,
                quantity = quantity,
                onAddToCart = { onAddToCart(quantity) },
                onBuyNow = { onBuyNow(quantity) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Product Images
            item {
                ProductImageSection(product.imageUrl)
            }

            // Product Info
            item {
                ProductInfoSection(
                    product = product,
                    ratingSummary = ratingSummary
                )
            }

            // Delivery Options
            item {
                DeliverySection(
                    selectedAddress = selectedAddress,
                    onChangeAddress = { showAddressDialog = true }
                )
            }

            // Quantity Selector
            item {
                QuantitySection(
                    quantity = quantity,
                    stock = product.stock,
                    onQuantityChange = { newQty ->
                        if (newQty in 1..product.stock) {
                            quantity = newQty
                        }
                    }
                )
            }

            // Tabs: Description, Reviews, Q&A
            item {
                TabSection(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // Tab Content
            item {
                when (selectedTab) {
                    0 -> DescriptionContent(product.description)
                    1 -> ReviewsContent(reviews, ratingSummary)
                    2 -> QuestionsContent()
                }
            }
        }
    }

    // Address Selection Dialog
    if (showAddressDialog && userAddresses.isNotEmpty()) {
        AddressSelectionDialog(
            addresses = userAddresses,
            selectedAddress = selectedAddress,
            onAddressSelected = {
                selectedAddress = it
                showAddressDialog = false
            },
            onDismiss = { showAddressDialog = false }
        )
    }
}

@Composable
fun ProductImageSection(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(Color(0xFFF5F5F5))
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Product Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun ProductInfoSection(
    product: ProductModel,
    ratingSummary: ProductRatingSummary
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Product Name
        Text(
            text = product.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < ratingSummary.averageRating.toInt())
                            Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${String.format("%.1f", ratingSummary.averageRating)}/5",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Ratings ${ratingSummary.totalRatings}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Price
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rs. ${product.price.toInt()}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B00)
            )

            if (product.price < product.price * 1.2) { // Mock discount
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Rs. ${(product.price * 1.2).toInt()}",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "-17%",
                    fontSize = 14.sp,
                    color = Color(0xFFFF6B00),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stock Status
        Text(
            text = if (product.stock > 0) "In Stock (${product.stock} available)" else "Out of Stock",
            fontSize = 14.sp,
            color = if (product.stock > 0) Color(0xFF00A651) else Color.Red,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DeliverySection(
    selectedAddress: String,
    onChangeAddress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Delivery Options",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedAddress.ifEmpty { "Select delivery address" },
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                TextButton(onClick = onChangeAddress) {
                    Text("CHANGE", color = Color(0xFF0B8FAC))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = Color(0xFF0B8FAC)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Standard Delivery",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Guaranteed by ${
                    SimpleDateFormat("d MMM", Locale.getDefault())
                        .format(Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000))
                }",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = Color(0xFF0B8FAC),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cash on Delivery Available",
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun QuantitySection(
    quantity: Int,
    stock: Int,
    onQuantityChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quantity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Decrease Button
                IconButton(
                    onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, Color.LightGray, CircleShape),
                    enabled = quantity > 1
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = if (quantity > 1) Color.Black else Color.Gray
                    )
                }

                // Quantity Display
                Text(
                    text = quantity.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Increase Button
                IconButton(
                    onClick = { if (quantity < stock) onQuantityChange(quantity + 1) },
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, Color.LightGray, CircleShape),
                    enabled = quantity < stock
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = if (quantity < stock) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun TabSection(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Description", "Reviews", "Questions")

    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.White,
        contentColor = Color(0xFF0B8FAC),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun DescriptionContent(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Product Description",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ReviewsContent(
    reviews: List<ReviewModel>,
    ratingSummary: ProductRatingSummary
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Rating Summary
        RatingSummaryCard(ratingSummary)

        Spacer(modifier = Modifier.height(16.dp))

        // Reviews List
        if (reviews.isEmpty()) {
            Text(
                text = "No reviews yet. Be the first to review!",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 32.dp)
            )
        } else {
            reviews.forEach { review ->
                ReviewCard(review)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun RatingSummaryCard(summary: ProductRatingSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Average Rating
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = String.format("%.1f", summary.averageRating),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < summary.averageRating.toInt())
                                Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "${summary.totalRatings} Ratings",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Rating Breakdown
            Column(
                modifier = Modifier.weight(2f)
            ) {
                RatingBar(5, summary.fiveStarCount, summary.totalRatings)
                RatingBar(4, summary.fourStarCount, summary.totalRatings)
                RatingBar(3, summary.threeStarCount, summary.totalRatings)
                RatingBar(2, summary.twoStarCount, summary.totalRatings)
                RatingBar(1, summary.oneStarCount, summary.totalRatings)
            }
        }
    }
}

@Composable
fun RatingBar(stars: Int, count: Int, total: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = "$stars",
            fontSize = 12.sp,
            modifier = Modifier.width(12.dp)
        )
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color(0xFFFFB800),
            modifier = Modifier.size(14.dp)
        )

        LinearProgressIndicator(
            progress = if (total > 0) count.toFloat() / total else 0f,
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .padding(horizontal = 8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFFFFB800),
            trackColor = Color.LightGray
        )

        Text(
            text = count.toString(),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
fun ReviewCard(review: ReviewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // User Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0B8FAC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.userName.firstOrNull()?.toString() ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = review.userName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (review.isVerifiedPurchase) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF00A651),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
                            .format(review.createdAt.toDate()),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                // Rating
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating)
                                Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (review.title.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = review.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.comment,
                fontSize = 13.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )

            // Review Images
            if (review.images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(review.images) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Review Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionsContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.QuestionAnswer,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "There are no questions yet.",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { /* Login/Register */ }) {
            Text("Login or Register to ask the seller now")
        }
    }
}

@Composable
fun BottomCartBar(
    price: Double,
    quantity: Int,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Add to Cart Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B00)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add to Cart", fontSize = 14.sp)
            }

            // Buy Now Button
            Button(
                onClick = onBuyNow,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B8FAC)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Buy Now", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun AddressSelectionDialog(
    addresses: List<String>,
    selectedAddress: String,
    onAddressSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Delivery Address") },
        text = {
            Column {
                addresses.forEach { address ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAddressSelected(address) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = address == selectedAddress,
                            onClick = { onAddressSelected(address) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = address, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}