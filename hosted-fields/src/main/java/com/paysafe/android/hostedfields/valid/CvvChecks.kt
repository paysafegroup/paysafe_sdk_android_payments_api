/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

internal class CvvChecks {

    companion object {
        private const val MAX_CHARS_FOR_CVV = 3
        private const val MAX_CHARS_FOR_AMEX_CVV = 4

        internal fun inputProtection(
            newCvv: String,
            newCardType: PSCreditCardType,
            onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
        ) = newCvv.filter { char ->
            val isCharValid = char.isDigit()
            if (!isCharValid) onEvent?.invoke(PSCardFieldInputEvent.INVALID_CHARACTER)
            isCharValid
        }.take(
            if (newCardType == PSCreditCardType.AMEX) MAX_CHARS_FOR_AMEX_CVV
            else MAX_CHARS_FOR_CVV
        )

        internal fun validations(
            cvvText: String,
            creditCardType: PSCreditCardType = PSCreditCardType.UNKNOWN
        ) = cvvText.isNotBlank()
                && cvvText.all { it.isDigit() }
                && if (creditCardType == PSCreditCardType.AMEX)
            cvvText.length == MAX_CHARS_FOR_AMEX_CVV else cvvText.length == MAX_CHARS_FOR_CVV
    }

}