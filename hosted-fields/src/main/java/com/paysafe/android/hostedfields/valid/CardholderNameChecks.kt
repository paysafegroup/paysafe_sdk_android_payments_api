/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.model.PSCardFieldInputEvent

internal class CardholderNameChecks {

    companion object {
        private const val SPACE_CHAR = ' '
        private const val EMPTY_STRING = ""
        private const val MAX_CARDHOLDER_NAME_CHARS = 24
        private val justWordsAndSpaces = Regex("^[A-Za-z ]+\$")

        private fun wasPreviousCharAndCurrentSpaces(
            previousText: String, currentIndex: Int, current: Char
        ) = currentIndex > 0 && previousText.last() == SPACE_CHAR && current == SPACE_CHAR

        internal fun inputProtection(
            newCardholderName: String,
            onEvent: ((PSCardFieldInputEvent) -> Unit)? = null
        ) = newCardholderName.filter { char ->
            val isCharValid = char.isLetter() || char.isWhitespace()
            if (!isCharValid) onEvent?.invoke(PSCardFieldInputEvent.INVALID_CHARACTER)
            isCharValid
        }.take(MAX_CARDHOLDER_NAME_CHARS).mapIndexed { charIndex, charInDate ->
            charIndex to charInDate
        }.fold(EMPTY_STRING) { previousText, (index, char) ->
            if (index == 0 && char == SPACE_CHAR) {
                EMPTY_STRING // Avoid space at the beginning
            } else if (wasPreviousCharAndCurrentSpaces(previousText, index, char)) {
                previousText + EMPTY_STRING // Avoid space after a previous space
            } else {
                previousText + char
            }
        }

        internal fun validations(cardHolderNameText: String) = cardHolderNameText.isNotBlank()
                && !cardHolderNameText.last().isWhitespace()
                && justWordsAndSpaces.matches(cardHolderNameText)
                && cardHolderNameText.length <= MAX_CARDHOLDER_NAME_CHARS
    }

}