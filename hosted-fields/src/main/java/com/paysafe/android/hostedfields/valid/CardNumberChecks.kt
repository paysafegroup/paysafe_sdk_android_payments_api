/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

internal class CardNumberChecks {

    companion object {
        private const val START_CHAR_AMEX = '3'
        private const val SECOND_CHAR_AMEX_ONE = '4'
        private const val SECOND_CHAR_AMEX_TWO = '7'
        private const val START_CHAR_VISA = '4'
        private const val START_CHAR_MASTERCARD = '5'
        private const val START_CHAR_DISCOVER = '6'

        internal const val CC_PLACEHOLDER_FOR_15 = "XXXX XXXXXX XXXXX"
        internal const val CC_PLACEHOLDER_FOR_16 = "XXXX XXXX XXXX XXXX"

        internal const val MAX_CHARS_FOR_AMEX_CARD = 15
        internal const val MAX_CHARS_FOR_CARD_NUMBERS = 16

        internal fun getCreditCardType(newCardNumber: String) = when (newCardNumber.firstOrNull()) {
            null -> PSCreditCardType.UNKNOWN
            START_CHAR_AMEX -> {
                if (newCardNumber.length >= 2) {
                    val secondChar = newCardNumber[1]
                    if (secondChar == SECOND_CHAR_AMEX_ONE || secondChar == SECOND_CHAR_AMEX_TWO) {
                        PSCreditCardType.AMEX
                    } else {
                        PSCreditCardType.UNKNOWN
                    }
                } else {
                    PSCreditCardType.UNKNOWN
                }
            }

            START_CHAR_VISA -> PSCreditCardType.VISA
            START_CHAR_MASTERCARD -> PSCreditCardType.MASTERCARD
            START_CHAR_DISCOVER -> PSCreditCardType.DISCOVER
            else -> PSCreditCardType.UNKNOWN
        }

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
            if (newCardType == PSCreditCardType.AMEX) MAX_CHARS_FOR_AMEX_CARD
            else MAX_CHARS_FOR_CARD_NUMBERS
        ).run {
            val newCardCreditType = getCreditCardType(this)
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
            if (cardNumberText[0] == START_CHAR_AMEX &&
                (cardNumberText[1] == SECOND_CHAR_AMEX_ONE
                        || cardNumberText[1] == SECOND_CHAR_AMEX_TWO)
            ) {
                return cardNumberText.length == MAX_CHARS_FOR_AMEX_CARD
            }
            return cardNumberText.length == MAX_CHARS_FOR_CARD_NUMBERS
        }

        internal fun validations(cardNumberText: String) = cardNumberText.isNotBlank()
                && isWithCorrectLength(cardNumberText)
                && isLuhn(cardNumberText)
    }

}