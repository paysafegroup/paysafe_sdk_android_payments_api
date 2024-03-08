/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.categories

data class UiProductCategoryData(
    val id: Int = 0,
    val title: String = "",
    val onClick: () -> Unit = {}
)