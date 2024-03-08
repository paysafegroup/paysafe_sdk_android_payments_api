/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.grid

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UiProductData(
    val id: Int = 0,
    val isFavorite: Boolean = false,
    val imageRes: Int = 0,
    val isNew: Boolean = false,
    val price: String = "$0.00",
    val name: String = "",
    val date: String = "",
    var quantity: String = "0",
    var totalRaw: Double = 0.0,
    var totalToDisplay: String = "$0.00",
    val description: String = "",
    @IgnoredOnParcel val onClick: (Int) -> Unit = {}
) : Parcelable {
    fun getContentDescription() = "${if (isNew) "New, " else ""}${price}, ${name}, $date"
}