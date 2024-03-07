/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.databinding.RvProductCategoryItemBinding

class ProductCategoriesAdapter(
    private val categoriesList: MutableList<UiProductCategoryData>
) : RecyclerView.Adapter<ProductCategoryViewHolder>() {

    override fun getItemCount(): Int = categoriesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProductCategoryViewHolder(
        RvProductCategoryItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        holder.bind(categoriesList[position])
    }

}