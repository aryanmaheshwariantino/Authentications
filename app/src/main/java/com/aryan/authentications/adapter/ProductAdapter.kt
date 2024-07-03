package com.aryan.authentications.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.aryan.authentications.databinding.ListItemProductBinding
import com.aryan.authentications.model.Product
import com.bumptech.glide.Glide

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ListItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ListItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.product = product // Bind data to layout
            Glide.with(binding.ivProductImage.context)
                .load(product.product_photo) // Assuming product_photo is a property in your Product model holding the image URL
                .into(binding.ivProductImage) // Bind the loaded image to ivProductImage
            binding.executePendingBindings()
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.asin == newItem.asin // Assuming `asin` is a unique identifier
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
