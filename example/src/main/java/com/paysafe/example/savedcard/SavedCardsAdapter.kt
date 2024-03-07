/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.savedcard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.databinding.RvSavedCardItemBinding

class SavedCardsAdapter(
    private var onItemClicked: (UiSavedCardData) -> Unit
) : RecyclerView.Adapter<SavedCardViewHolder>() {

    private var savedCardsList: List<UiSavedCardData> = emptyList()

    override fun getItemCount(): Int = savedCardsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SavedCardViewHolder(
        binding = RvSavedCardItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onItemClicked = onItemClicked
    )

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.bind(savedCardsList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(data: List<UiSavedCardData>) {
        this.savedCardsList = data
        notifyDataSetChanged()
    }
}