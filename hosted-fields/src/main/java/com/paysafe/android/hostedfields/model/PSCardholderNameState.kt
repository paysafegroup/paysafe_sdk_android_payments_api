/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.runtime.Stable

@Stable
interface PSCardholderNameState {
    var value: String
    var isFocused: Boolean
    var isValidInUi: Boolean
    var alreadyShown: Boolean

    fun isEmpty(): Boolean
    fun isValid(): Boolean
    fun showLabelWithoutAnimation(animateTopLabelText: Boolean, labelText: String): Boolean
}