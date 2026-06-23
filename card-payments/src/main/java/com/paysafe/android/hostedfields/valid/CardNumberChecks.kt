/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

internal class CardNumberChecks {

    companion object {
        internal const val CC_PLACEHOLDER_FOR_15 = "XXXX XXXXXX XXXXX"
        internal const val CC_PLACEHOLDER_FOR_16 = "XXXX XXXX XXXX XXXX"

        internal const val MAX_CHARS_FOR_CARD_NUMBERS = 16

        private fun getCreditCardPlaceholder(
            newCardCreditType: PSCreditCardType
        ) = when (newCardCreditType) {
            PSCreditCardType.AMEX -> CC_PLACEHOLDER_FOR_15
            else -> CC_PLACEHOLDER_FOR_16
        }

        fun inputProtection(
            newCardNumber: String,
            newCardType: PSCreditCardType,
            onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
        ) = newCardNumber.filter { char ->
            val isCharValid = char.isDigit()
            if (!isCharValid) onEvent?.invoke(PSCardFieldInputEvent.INVALID_CHARACTER)
            isCharValid
        }.take(
            newCardType.getCardInfo()?.maxLength ?: MAX_CHARS_FOR_CARD_NUMBERS
        ).run {
            val newCardCreditType = this.getCardInfo()?.type ?: PSCreditCardType.UNKNOWN
            Triple(this, newCardCreditType, getCreditCardPlaceholder(newCardCreditType))
        }

        private fun isLuhn(numberInput: String): Boolean {
            val digits = numberInput.filter {
                it.isDigit()
            }.reversed().map {
                it.toString().toInt()
            }
            if (numberInput.length != digits.size) return false

            return digits.mapIndexed { index, digit ->
                var accumulator = digit
                val isIndexOdd = index % 2 == 1
                if (isIndexOdd) {
                    accumulator *= 2
                }
                if (accumulator > 9) {
                    accumulator -= 9
                }
                accumulator
            }.sum() % 10 == 0
        }

        private fun isWithCorrectLength(cardNumberText: String): Boolean {
            if (cardNumberText.length <= 2) return false

            return (cardNumberText.getCardInfo()?.maxLength ?: MAX_CHARS_FOR_CARD_NUMBERS) == cardNumberText.length
        }

        internal fun validations(cardNumberText: String) = cardNumberText.isNotBlank()
                && isWithCorrectLength(cardNumberText)
                && isLuhn(cardNumberText)
    }


}