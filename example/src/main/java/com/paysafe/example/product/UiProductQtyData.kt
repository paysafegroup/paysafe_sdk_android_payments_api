/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.product

data class UiProductQtyData(
    val id: Int = 0,
    val quantity: String = "0",
    var isSelected: Boolean = false,
    val onClick: (Int, String) -> Unit = { _: Int, _: String -> }
)