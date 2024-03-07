/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.savedcard

import android.os.Parcelable
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import kotlinx.parcelize.Parcelize

@Parcelize
data class UiSavedCardData(
    val cardBrandRes: Int = 0,
    val cardBrandType: PSCreditCardType = PSCreditCardType.UNKNOWN,
    val lastDigits: String = "",
    val holderName: String = "",
    val expiryMonth: String = "",
    val expiryYear: String = "",
    val expiryDate: String = "",
    val paymentHandleTokenFrom: String = "",
    val singleUseCustomerToken: String = "",
) : Parcelable