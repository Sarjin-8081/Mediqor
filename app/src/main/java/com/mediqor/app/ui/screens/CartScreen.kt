package com.mediqor.app.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class CartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CartScreen()
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
                Text(
                    text = "My Cart (2)",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "DELETE",
                    color = Color.White
                )
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
            var note by remember { mutableStateOf("") }
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
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
    var isChecked by remember { mutableStateOf(true) }

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

            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = brand,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
                Text(
                    text = "$price | DELETE",
                    color = Color.Red,
                    fontSize = 13.sp
                )
            }

            QuantityButton()
        }
    }
}

@Composable
fun QuantityButton() {
    var quantity by remember { mutableStateOf(1) }

    Row(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "-",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = quantity.toString())
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "+",
            fontSize = 18.sp
        )
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
            Text(
                text = "2 Items | ₹ 1822.60",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "You save ₹ 0",
                color = Color.Green,
                fontSize = 13.sp
            )
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