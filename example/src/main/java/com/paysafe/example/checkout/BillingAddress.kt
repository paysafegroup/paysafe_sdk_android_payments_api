/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.checkout

data class BillingAddress(
    val firstName: String = "",
    val lastName: String = "",
    val streetAddress: String = "",
    val city: String = "",
    val state: String = "",
    val zip: String = ""
) {
    fun formattedAddress() = "${firstName}\n${lastName}\n${streetAddress}\n${city}, ${state}, $zip"
}