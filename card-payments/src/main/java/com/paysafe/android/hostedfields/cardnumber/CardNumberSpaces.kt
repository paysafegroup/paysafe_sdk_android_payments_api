/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import com.paysafe.android.hostedfields.domain.model.CardNumberSeparator
import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.MAX_CHARS_FOR_AMEX_CARD
import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.MAX_CHARS_FOR_CARD_NUMBERS

internal object CardNumberSpaces {

    private const val NUMBERS_BEFORE_FIRST_SEPARATOR = 4
    private const val NUMBERS_BEFORE_SECOND_SEPARATOR_DEFAULT = 8
    private const val NUMBERS_BEFORE_SECOND_SEPARATOR_AMEX = 10
    private const val NUMBERS_BEFORE_THIRD_SEPARATOR_DEFAULT = 12

    var separator: CardNumberSeparator = CardNumberSeparator.WHITESPACE
    private var allNumbersWithSeparatorAmex = MAX_CHARS_FOR_AMEX_CARD + separator.length() * 2
    private var allNumberWithSeparatorDefault = MAX_CHARS_FOR_CARD_NUMBERS + separator.length() * 3

    val amexSpacesMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (separator.length() == 0) return offset
            if (offset <= NUMBERS_BEFORE_FIRST_SEPARATOR) return offset
            if (offset <= NUMBERS_BEFORE_SECOND_SEPARATOR_AMEX) return offset + 1
            if (offset <= MAX_CHARS_FOR_AMEX_CARD) return offset + 2
            return allNumbersWithSeparatorAmex
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (separator.length() == 0) return offset
            if (offset <= NUMBERS_BEFORE_FIRST_SEPARATOR) return offset
            if (offset <= NUMBERS_BEFORE_SECOND_SEPARATOR_AMEX + 1) return offset - 1
            if (offset <= allNumbersWithSeparatorAmex) return offset - 2
            return MAX_CHARS_FOR_AMEX_CARD
        }
    }

    val defaultSpacesMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (separator.length() == 0) return offset
            if (offset <= NUMBERS_BEFORE_FIRST_SEPARATOR) return offset
            if (offset <= NUMBERS_BEFORE_SECOND_SEPARATOR_DEFAULT) return offset + 1
            if (offset <= NUMBERS_BEFORE_THIRD_SEPARATOR_DEFAULT) return offset + 2
            if (offset <= MAX_CHARS_FOR_CARD_NUMBERS) return offset + 3
            return allNumberWithSeparatorDefault
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (separator.length() == 0) return offset
            if (offset <= NUMBERS_BEFORE_FIRST_SEPARATOR) return offset
            if (offset <= NUMBERS_BEFORE_SECOND_SEPARATOR_DEFAULT + 1) return offset - 1
            if (offset <= NUMBERS_BEFORE_THIRD_SEPARATOR_DEFAULT + 2) return offset - 2
            if (offset <= allNumberWithSeparatorDefault) return offset - 3
            return MAX_CHARS_FOR_CARD_NUMBERS
        }
    }

    fun formatAmexWithSeparator(amexCardNumbers: AnnotatedString) = buildAnnotatedString {
        for (amexNumberIndex in amexCardNumbers.indices) {
            append(amexCardNumbers[amexNumberIndex])
            separator.toChar()?.let { separatorChar ->
                if (((amexNumberIndex + 1) == NUMBERS_BEFORE_FIRST_SEPARATOR ||
                            (amexNumberIndex + 1) == NUMBERS_BEFORE_SECOND_SEPARATOR_AMEX) &&
                    amexNumberIndex + 1 != amexCardNumbers.length
                ) {
                    append(separatorChar)
                }
            }
        }
    }

    fun formatDefaultWithSeparator(cardNumbers: AnnotatedString) = buildAnnotatedString {
        for (numberIndex in cardNumbers.indices) {
            append(cardNumbers[numberIndex])
            separator.toChar()?.let { separatorChar ->
                if ((numberIndex + 1) % NUMBERS_BEFORE_FIRST_SEPARATOR == 0
                    && numberIndex < cardNumbers.length - 1
                ) {
                    append(separatorChar)
                }
            }
        }
    }

    private fun CardNumberSeparator.toChar(): Char? = when (this) {
        CardNumberSeparator.WHITESPACE -> ' '
        CardNumberSeparator.NONE -> null
        CardNumberSeparator.DASH -> '-'
        CardNumberSeparator.SLASH -> '/'
    }

    internal fun CardNumberSeparator.length(): Int = when (this) {
        CardNumberSeparator.NONE -> 0
        CardNumberSeparator.WHITESPACE,
        CardNumberSeparator.DASH,
        CardNumberSeparator.SLASH -> 1
    }

}