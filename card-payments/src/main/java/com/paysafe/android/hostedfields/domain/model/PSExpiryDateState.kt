/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

import androidx.compose.runtime.Stable

@Stable
interface PSExpiryDateState {
    var value: String
    var isFocused: Boolean
    var isValidInUi: Boolean
    var isPickerOpen: Boolean
    var alreadyShown: Boolean

    fun isEmpty(): Boolean
    fun isValid(): Boolean
    fun showLabelWithoutAnimation(animateTopLabelText: Boolean, labelText: String): Boolean
}