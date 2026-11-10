/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent

internal class CardholderNameChecks {

    companion object {
        private const val MAX_CARDHOLDER_NAME_CHARS = 24
        private val justWordsAndSpaces = Regex("[A-Za-zÀ-ÿ' -]{2,}")
        private val symbolValidation = Regex("[A-Za-zÀ-ÿ' -]+")

        internal fun inputProtection(
            newCardholderName: String,
            onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
        ): String {
            if (!symbolValidation.matches(newCardholderName)) {
                onEvent?.invoke(PSCardFieldInputEvent.INVALID_CHARACTER)
            }
            return newCardholderName.take(MAX_CARDHOLDER_NAME_CHARS).trimStart()
        }

        internal fun validations(cardHolderNameText: String) = cardHolderNameText.isNotBlank()
                && justWordsAndSpaces.matches(cardHolderNameText)
                && cardHolderNameText.length <= MAX_CARDHOLDER_NAME_CHARS
    }

}