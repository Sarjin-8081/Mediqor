package com.mediqor.app.ui.screens.cart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.ui.theme.MediQorTheme

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediQorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CartScreen()
                }
            }
        }
    }
}

@Composable
fun CartScreen() {
    Scaffold(
        bottomBar = { CartBottomBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5FFF9))
                .padding(padding)
        ) {

            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF5EC9A7))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("My Cart (2)", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Text("DELETE", color = Color.White)
            }

            Spacer(modifier = Modifier.height(10.dp))

            CartItem(
                name = "Acmist Moisturizing Cream Gel 50gm",
                brand = "Brinton Pharmaceuticals Ltd",
                price = "₹ 697.60"
            )

            CartItem(
                name = "Sure Grow Minoxidil 5% 60ml",
                brand = "Sure Grow",
                price = "₹ 1125.00"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Note field
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Note for the pharmacist") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun CartItem(name: String, brand: String, price: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFDFF7EA))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(checked = true, onCheckedChange = {})

            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(brand, color = Color.Gray, fontSize = 13.sp)
                Text("$price | DELETE", color = Color.Red, fontSize = 13.sp)
            }

            QuantityButton()
        }
    }
}

@Composable
fun QuantityButton() {
    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("-", fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text("1")
        Spacer(modifier = Modifier.width(10.dp))
        Text("+", fontSize = 18.sp)
    }
}

@Composable
fun CartBottomBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("2 Items | ₹ 1822.60", fontWeight = FontWeight.Bold)
            Text("You save ₹ 0", color = Color.Green, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2FB36D))
        ) {
            Text("CHECKOUT")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    CartScreen()
}
