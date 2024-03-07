/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.grid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.databinding.RvProductItemBinding

class ProductsAdapter(
    private val productsList: MutableList<UiProductData>
) : RecyclerView.Adapter<ProductViewHolder>() {

    fun getProduct(index: Int) = productsList[index]

    override fun getItemCount(): Int = productsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductViewHolder(
        RvProductItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productsList[position], position)
    }

}