/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import com.paysafe.android.hostedfields.util.CARD_NUMBER_ALREADY_SHOWN_INDEX
import com.paysafe.android.hostedfields.util.CARD_NUMBER_FOCUSED_INDEX
import com.paysafe.android.hostedfields.util.CARD_NUMBER_PLACEHOLDER_INDEX
import com.paysafe.android.hostedfields.util.CARD_NUMBER_TYPE_INDEX
import com.paysafe.android.hostedfields.util.CARD_NUMBER_VALID_INDEX
import com.paysafe.android.hostedfields.util.CARD_NUMBER_VALUE_INDEX
import com.paysafe.android.hostedfields.valid.CardNumberChecks
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

class PSCardNumberStateImpl(
    value: String = "",
    isFocused: Boolean = false,
    type: PSCreditCardType = PSCreditCardType.UNKNOWN,
    placeholder: String = "",
    isValidInUi: Boolean = true,
    alreadyShown: Boolean = false
) : PSCardNumberState {

    override var value: String by mutableStateOf(value)
    override var isFocused: Boolean by mutableStateOf(isFocused)
    override var type: PSCreditCardType by mutableStateOf(type)
    override var placeholder: String by mutableStateOf(placeholder)
    override var isValidInUi: Boolean by mutableStateOf(isValidInUi)
    override var alreadyShown: Boolean by mutableStateOf(alreadyShown)

    override fun isEmpty() = value.isEmpty()
    override fun isValid() = CardNumberChecks.validations(value)
    override fun showLabelWithoutAnimation(
        animateTopLabelText: Boolean,
        labelText: String
    ) = !animateTopLabelText && !isFocused && value.isBlank()

    companion object {
        val Saver = Saver<PSCardNumberStateImpl, List<Any>>(
            save = {
                listOf(
                    it.value,
                    it.isFocused,
                    it.type,
                    it.placeholder,
                    it.isValidInUi,
                    it.alreadyShown
                )
            },
            restore = {
                PSCardNumberStateImpl(
                    it[CARD_NUMBER_VALUE_INDEX] as String,
                    it[CARD_NUMBER_FOCUSED_INDEX] as Boolean,
                    it[CARD_NUMBER_TYPE_INDEX] as PSCreditCardType,
                    it[CARD_NUMBER_PLACEHOLDER_INDEX] as String,
                    it[CARD_NUMBER_VALID_INDEX] as Boolean,
                    it[CARD_NUMBER_ALREADY_SHOWN_INDEX] as Boolean
                )
            }
        )
    }

}