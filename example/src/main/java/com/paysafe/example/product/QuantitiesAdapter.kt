/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.databinding.RvQuantityItemBinding

class QuantitiesAdapter(
    private val productQuantitiesList: MutableList<UiProductQtyData>
) : RecyclerView.Adapter<QuantityViewHolder>() {

    override fun getItemCount(): Int = productQuantitiesList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuantityViewHolder(
        RvQuantityItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: QuantityViewHolder, position: Int) {
        holder.bind(productQuantitiesList[position], position)
    }

    fun selectQuantity(newSelectedIndex: Int) {
        productQuantitiesList[newSelectedIndex].isSelected = true
        notifyItemChanged(newSelectedIndex)
    }

    fun unselectQuantity(oldSelectedIndex: Int) {
        productQuantitiesList[oldSelectedIndex].isSelected = false
        notifyItemChanged(oldSelectedIndex)
    }

}