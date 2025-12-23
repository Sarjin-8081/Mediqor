package com.mediqor.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mediqor.app.model.Category

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(category.icon),
                contentDescription = category.title,
                modifier = Modifier.size(45.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.title, fontSize = 13.sp)
        }
    }
}
