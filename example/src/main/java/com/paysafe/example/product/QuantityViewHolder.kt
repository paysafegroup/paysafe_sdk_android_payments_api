/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.product

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.R
import com.paysafe.example.databinding.RvQuantityItemBinding

class QuantityViewHolder(binding: RvQuantityItemBinding) : RecyclerView.ViewHolder(binding.root) {
    private val quantity: TextView = binding.quantityText
    private val selected: View = binding.quantitySelectedLine

    fun bind(uiData: UiProductQtyData, index: Int) {
        itemView.id = uiData.id
        val previousTypeface = quantity.typeface

        quantity.text = uiData.quantity
        if (uiData.isSelected) {
            selected.visibility = View.VISIBLE
            quantity.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.grid_product_main
                )
            )
            quantity.setTypeface(previousTypeface, Typeface.BOLD)
        } else {
            selected.visibility = View.GONE
            quantity.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.grid_product_date
                )
            )
            quantity.setTypeface(previousTypeface, Typeface.NORMAL)
        }
        itemView.setOnClickListener { uiData.onClick(index, quantity.text.toString()) }
    }
}