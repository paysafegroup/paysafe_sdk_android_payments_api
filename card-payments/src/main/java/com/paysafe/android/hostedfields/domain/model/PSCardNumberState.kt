/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

import androidx.compose.runtime.Stable
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

@Stable
interface PSCardNumberState {
    var value: String
    var isFocused: Boolean
    var type: PSCreditCardType
    var placeholder: String
    var isValidInUi: Boolean
    var alreadyShown: Boolean

    fun isEmpty(): Boolean
    fun isValid(): Boolean
    fun showLabelWithoutAnimation(animateTopLabelText: Boolean, labelText: String): Boolean
}