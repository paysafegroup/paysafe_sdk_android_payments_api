/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.grid

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.R
import com.paysafe.example.databinding.RvProductItemBinding

class ProductViewHolder(binding: RvProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private val favoriteImg: ImageView = binding.productFavoriteImg
    private val img: ImageView = binding.productImg
    private val isNew: FrameLayout = binding.productIsNew
    private val price: TextView = binding.productPriceText
    private val name: TextView = binding.productNameText
    private val date: TextView = binding.productDateText

    fun bind(uiData: UiProductData, index: Int) {
        itemView.id = uiData.id
        if (uiData.isFavorite) {
            favoriteImg.setImageResource(R.drawable.ic_fav_filled)
        } else {
            favoriteImg.setImageResource(R.drawable.ic_fav_outlined)
        }
        img.setImageResource(uiData.imageRes)
        if (uiData.isNew) {
            isNew.visibility = View.VISIBLE
        }
        price.text = uiData.price
        name.text = uiData.name
        date.text = uiData.date
        itemView.contentDescription = uiData.getContentDescription()
        itemView.setOnClickListener { uiData.onClick(index) }
    }
}