/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import com.paysafe.android.hostedfields.util.CARDHOLDER_NAME_ALREADY_SHOWN_INDEX
import com.paysafe.android.hostedfields.util.CARDHOLDER_NAME_FOCUSED_INDEX
import com.paysafe.android.hostedfields.util.CARDHOLDER_NAME_VALID_INDEX
import com.paysafe.android.hostedfields.util.CARDHOLDER_NAME_VALUE_INDEX
import com.paysafe.android.hostedfields.valid.CardholderNameChecks

class PSCardholderNameStateImpl(
    value: String = "",
    isFocused: Boolean = false,
    isValidInUi: Boolean = true,
    alreadyShown: Boolean = false
) : PSCardholderNameState {

    override var value: String by mutableStateOf(value)
    override var isFocused: Boolean by mutableStateOf(isFocused)
    override var isValidInUi: Boolean by mutableStateOf(isValidInUi)
    override var alreadyShown: Boolean by mutableStateOf(alreadyShown)

    override fun isEmpty() = value.isEmpty()
    override fun isValid() = CardholderNameChecks.validations(value)
    override fun showLabelWithoutAnimation(
        animateTopLabelText: Boolean,
        labelText: String
    ) = !animateTopLabelText && !isFocused && value.isBlank()

    companion object {
        val Saver = Saver<PSCardholderNameStateImpl, List<Any>>(
            save = { listOf(it.value, it.isFocused, it.isValidInUi, it.alreadyShown) },
            restore = {
                PSCardholderNameStateImpl(
                    it[CARDHOLDER_NAME_VALUE_INDEX] as String,
                    it[CARDHOLDER_NAME_FOCUSED_INDEX] as Boolean,
                    it[CARDHOLDER_NAME_VALID_INDEX] as Boolean,
                    it[CARDHOLDER_NAME_ALREADY_SHOWN_INDEX] as Boolean
                )
            }
        )
    }

}