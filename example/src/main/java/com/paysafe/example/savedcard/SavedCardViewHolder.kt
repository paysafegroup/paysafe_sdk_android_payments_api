/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.savedcard

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.databinding.RvSavedCardItemBinding

class SavedCardViewHolder(
    binding: RvSavedCardItemBinding,
    private var onItemClicked: (UiSavedCardData) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val cardBrandImg: ImageView = binding.savedCardCCardBrand
    private val lastDigits: TextView = binding.savedCardLastDigits
    private val holderName: TextView = binding.savedCardHolderName
    private val expiryDate: TextView = binding.savedCardExpiryDate

    fun bind(uiData: UiSavedCardData) {
        cardBrandImg.setImageResource(uiData.cardBrandRes)
        lastDigits.text = "*${uiData.lastDigits}"
        holderName.text = uiData.holderName
        expiryDate.text = uiData.expiryDate
        itemView.setOnClickListener { onItemClicked(uiData) }
    }
}