// view/screens/FamilyCareActivity.kt
package com.example.mediqorog.view.screens

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediqorog.R
import com.example.mediqorog.model.Product
import com.example.mediqorog.viewmodel.CategoryProductsViewModel

class FamilyCareActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryProductsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacy) // Reuse the same layout
        viewModel = ViewModelProvider(this)[CategoryProductsViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadProducts("Family Care")
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts)
        backButton = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            displayProducts(products)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun displayProducts(products: List<Product>) {
        val adapter = object : RecyclerView.Adapter<ProductViewHolder>() {
            override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ProductViewHolder {
                val view = layoutInflater.inflate(R.layout.item_product, parent, false)
                return ProductViewHolder(view)
            }

            override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
                holder.bind(products[position])
            }

            override fun getItemCount() = products.size
        }

        recyclerView.adapter = adapter
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: android.widget.ImageView = itemView.findViewById(R.id.ivProductImage)
        private val productName: TextView = itemView.findViewById(R.id.tvProductName)
        private val productDescription: TextView = itemView.findViewById(R.id.tvProductDescription)
        private val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        private val btnAddToCart: android.widget.ImageView = itemView.findViewById(R.id.btnAddToCart)

        fun bind(product: Product) {
            productName.text = product.name
            productDescription.text = product.description
            productPrice.text = "NPR ${product.price.toInt()}"
            productImage.setImageResource(R.drawable.ic_product_box)

            itemView.setOnClickListener {
                // Handle product click
            }

            btnAddToCart.setOnClickListener {
                // Handle add to cart
            }
        }
    }
}