package com.example.mediqorog.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediqorog.R
import com.example.mediqorog.model.ProductModel
import com.example.mediqorog.viewmodel.CategoryProductsViewModel

class PharmacyActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryProductsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pharmacy)

        viewModel = ViewModelProvider(this)[CategoryProductsViewModel::class.java]

        initViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadProducts("Pharmacy")
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts)
        backButton = findViewById(R.id.btnBack)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ProductsAdapter(emptyList())
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            (recyclerView.adapter as ProductsAdapter)
                .updateProducts(products ?: emptyList())
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

    // ================= Adapter =================
    inner class ProductsAdapter(
        private var products: List<ProductModel>
    ) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

        fun updateProducts(newProducts: List<ProductModel>) {
            products = newProducts
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount(): Int = products.size

        // ================= ViewHolder =================
        inner class ProductViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            private val productImage: TextView =
                itemView.findViewById(R.id.ivProductImage)
            private val productName: TextView =
                itemView.findViewById(R.id.tvProductName)
            private val productDescription: TextView =
                itemView.findViewById(R.id.tvProductDescription)
            private val productPrice: TextView =
                itemView.findViewById(R.id.tvProductPrice)
            private val btnAddToCart: TextView =
                itemView.findViewById(R.id.btnAddToCart)

            fun bind(product: ProductModel) {
                productName.text = product.name
                productDescription.text = product.description
                productPrice.text = "NPR ${product.price.toInt()}"
                productImage.text = "📦"

                itemView.setOnClickListener {
                    // Product click
                }

                btnAddToCart.setOnClickListener {
                    // Add to cart
                }
            }
        }
    }
}
