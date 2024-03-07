/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import com.paysafe.android.hostedfields.util.CVV_ALREADY_SHOWN_INDEX
import com.paysafe.android.hostedfields.util.CVV_CARD_TYPE_INDEX
import com.paysafe.android.hostedfields.util.CVV_VALID_INDEX
import com.paysafe.android.hostedfields.util.CVV_VALUE_INDEX
import com.paysafe.android.hostedfields.valid.CvvChecks
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

class PSCvvStateImpl(
    value: String = "",
    type: PSCreditCardType = PSCreditCardType.UNKNOWN,
    isValidInUi: Boolean = true,
    alreadyShown: Boolean = false
) : PSCvvState {

    override var value: String by mutableStateOf(value)
    override var cardType: PSCreditCardType by mutableStateOf(type)
    override var isValidInUi: Boolean by mutableStateOf(isValidInUi)
    override var alreadyShown: Boolean by mutableStateOf(alreadyShown)

    override fun isEmpty() = value.isEmpty()
    override fun isValid() = CvvChecks.validations(value, cardType)

    companion object {
        val Saver = Saver<PSCvvStateImpl, List<Any>>(
            save = { listOf(it.value, it.cardType, it.isValidInUi, it.alreadyShown) },
            restore = {
                PSCvvStateImpl(
                    it[CVV_VALUE_INDEX] as String,
                    it[CVV_CARD_TYPE_INDEX] as PSCreditCardType,
                    it[CVV_VALID_INDEX] as Boolean,
                    it[CVV_ALREADY_SHOWN_INDEX] as Boolean
                )
            }
        )
    }

}