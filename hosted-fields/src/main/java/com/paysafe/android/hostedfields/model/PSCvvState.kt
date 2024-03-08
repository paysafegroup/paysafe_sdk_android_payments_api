/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.runtime.Stable
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

@Stable
interface PSCvvState {
    var value: String
    var isFocused: Boolean
    var cardType: PSCreditCardType
    var isValidInUi: Boolean
    var alreadyShown: Boolean

    fun isEmpty(): Boolean
    fun isValid(): Boolean
    fun showLabelWithoutAnimation(animateTopLabelText: Boolean, labelText: String): Boolean
}