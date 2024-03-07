/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.runtime.Stable
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

@Stable
interface PSCardNumberState {
    var value: String
    var type: PSCreditCardType
    var placeholder: String
    var isValidInUi: Boolean
    var alreadyShown: Boolean

    fun isEmpty(): Boolean
    fun isValid(): Boolean
}