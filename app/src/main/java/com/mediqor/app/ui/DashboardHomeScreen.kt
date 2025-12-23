package com.mediqor.app.ui

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mediqor.app.ui.view.DevicesActivity
import com.mediqor.app.ui.view.FamilyCareActivity
import com.mediqor.app.ui.view.PersonalCareActivity
import com.mediqor.app.ui.view.PharmacyActivity
import com.mediqor.app.ui.view.SupplementsActivity
import com.mediqor.app.ui.view.SurgicalActivity
import com.mediqor.app.viewmodel.HomeViewModel

class DashboardHomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Initialize ViewModel here once
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(viewModel = homeViewModel)
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ---------------- Categories ----------------
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(260.dp),
                userScrollEnabled = false
            ) {
                items(viewModel.categories) { category ->
                    CategoryCard(category) {

                        when (category.title) {
                            "Pharmacy" ->
                                context.startActivity(
                                    Intent(context, PharmacyActivity::class.java)
                                )

                            "Family Care" ->
                                context.startActivity(
                                    Intent(context, FamilyCareActivity::class.java)
                                )

                            "Personal Care" ->
                                context.startActivity(
                                    Intent(context, PersonalCareActivity::class.java)
                                )

                            "Supplements" ->
                                context.startActivity(
                                    Intent(context, SupplementsActivity::class.java)
                                )

                            "Surgical" ->
                                context.startActivity(
                                    Intent(context, SurgicalActivity::class.java)
                                )

                            "Devices" ->
                                context.startActivity(
                                    Intent(context, DevicesActivity::class.java)
                                )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

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

        // ---------------- Products ----------------
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(520.dp),
                userScrollEnabled = false
            ) {
                items(viewModel.products) { product ->
                    ProductCard(product)
                }
            }
        }
    }
}
