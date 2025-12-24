package com.mediqor.app.ui.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mediqor.app.R
import com.mediqor.app.model.ProductModel
import com.mediqor.app.repository.CommonRepoImpl
import com.mediqor.app.repository.ProductRepoImpl
import com.mediqor.app.utils.ImageUtils
import com.mediqor.app.viewmodel.CommonViewModel
import com.mediqor.app.viewmodel.ProductViewModel

class AddProductActivity : ComponentActivity() {

    private lateinit var imageUtils: ImageUtils
    private var selectedImageUri by mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            selectedImageUri = uri
        }

        setContent {
            AddProductBody(
                selectedImageUri = selectedImageUri,
                onPickImage = { imageUtils.launchImagePicker() }
            )
        }
    }
}

@Composable
fun AddProductBody(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit
) {
    var pName by remember { mutableStateOf("") }
    var pPrice by remember { mutableStateOf("") }
    var pDesc by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    val repo = remember { ProductRepoImpl() }
    val viewModel = remember { ProductViewModel(repo) }
    val common = remember { CommonViewModel(CommonRepoImpl()) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {

                // IMAGE PICKER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onPickImage() }
                        .padding(10.dp)
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.billy),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // NAME
                OutlinedTextField(
                    value = pName,
                    onValueChange = { pName = it },
                    placeholder = { Text("Enter product name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // PRICE
                OutlinedTextField(
                    value = pPrice,
                    onValueChange = { pPrice = it },
                    placeholder = { Text("Enter price") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // DESCRIPTION
                OutlinedTextField(
                    value = pDesc,
                    onValueChange = { pDesc = it },
                    placeholder = { Text("Enter description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ADD BUTTON
                Button(
                    onClick = {
                        if (pName.isBlank() || pPrice.isBlank() || pDesc.isBlank()) {
                            Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val price = pPrice.toDoubleOrNull()
                        if (price == null) {
                            Toast.makeText(context, "Invalid price", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (selectedImageUri == null) {
                            Toast.makeText(context, "Select image first", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        loading = true
                        common.uploadImage(context, selectedImageUri) { imageUrl ->
                            if (imageUrl != null) {
                                val model = ProductModel(
                                    id = "",
                                    name = pName,
                                    price = price,
                                    description = pDesc,
                                    imageUrl = imageUrl
                                )

                                viewModel.addProduct(model) { success, message ->
                                    loading = false
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    if (success) activity?.finish()
                                }
                            } else {
                                loading = false
                                Log.e("Upload", "Image upload failed")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loading
                ) {
                    Text(if (loading) "Uploading..." else "Add Product")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddProduct() {
    AddProductBody(
        selectedImageUri = null,
        onPickImage = {}
    )
}
