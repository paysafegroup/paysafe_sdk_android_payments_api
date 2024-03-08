/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.categories

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.databinding.RvProductCategoryItemBinding

class ProductCategoryViewHolder(
    binding: RvProductCategoryItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val title: TextView = binding.productCategoryTitle

    fun bind(uiData: UiProductCategoryData) {
        itemView.id = uiData.id
        title.text = uiData.title
        itemView.setOnClickListener { uiData.onClick() }
    }
}