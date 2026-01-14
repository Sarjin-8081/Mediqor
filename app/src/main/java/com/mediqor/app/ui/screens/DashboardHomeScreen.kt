package com.mediqor.app.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediqor.app.ui.CategoryCard
import com.mediqor.app.ui.ProductCard
import com.mediqor.app.ui.view.category.DevicesActivity
import com.mediqor.app.ui.view.category.FamilyCareActivity
import com.mediqor.app.ui.view.category.PersonalCareActivity
import com.mediqor.app.ui.view.category.PharmacyActivity
import com.mediqor.app.ui.view.category.SupplementsActivity
import com.mediqor.app.ui.view.category.SurgicalActivity
import com.mediqor.app.viewmodel.HomeViewModel

class DashboardHomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(viewModel = homeViewModel)
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // ---------------- Categories ----------------
            item {
                Text(
                    text = "Categories",
                    fontSize = 22.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    userScrollEnabled = false,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.categories) { category ->
                        CategoryCard(category) {
                            when (category.title) {
                                "Pharmacy" ->
                                    context.startActivity(Intent(context, PharmacyActivity::class.java))
                                "Family Care" ->
                                    context.startActivity(Intent(context, FamilyCareActivity::class.java))
                                "Personal Care" ->
                                    context.startActivity(Intent(context, PersonalCareActivity::class.java))
                                "Supplements" ->
                                    context.startActivity(Intent(context, SupplementsActivity::class.java))
                                "Surgical" ->
                                    context.startActivity(Intent(context, SurgicalActivity::class.java))
                                "Devices" ->
                                    context.startActivity(Intent(context, DevicesActivity::class.java))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // ---------------- SHOP ----------------
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFFAAD9D1), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SHOP", fontSize = 20.sp, color = Color.White)
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // ---------------- Products Grid ----------------
            viewModel.products.chunked(2).forEach { rowProducts ->
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            Box(modifier = Modifier.weight(1f)) {
                                ProductCard(product)
                            }
                        }
                        // Add empty box if odd number of products in last row
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // space for FAB
        }

        // ---------------- ADD PRODUCT (+) BUTTON ----------------
        FloatingActionButton(
            onClick = {
                context.startActivity(
                    Intent(context, AddProductActivity::class.java)
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF4CAF50)
        ) {
            Text("+", fontSize = 28.sp, color = Color.White)
        }
    }
}