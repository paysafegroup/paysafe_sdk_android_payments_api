/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.MONTH_YEAR_SEPARATOR

internal class ExpiryDateSlash {

    companion object {
        private const val END_OF_2ND_DIGIT = 2
        private const val FIRST_SPACE_AFTER_MONTH = 3

        val slashMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= END_OF_2ND_DIGIT) return offset
                return offset + MONTH_YEAR_SEPARATOR.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when (offset) {
                    in 0..END_OF_2ND_DIGIT -> offset
                    in FIRST_SPACE_AFTER_MONTH..5 -> END_OF_2ND_DIGIT
                    else -> offset - MONTH_YEAR_SEPARATOR.length
                }
            }
        }

        internal fun formatWithSlash(dateText: AnnotatedString) = buildAnnotatedString {
            if (dateText.length >= FIRST_SPACE_AFTER_MONTH) {
                append(dateText.substring(0, END_OF_2ND_DIGIT))
                append(MONTH_YEAR_SEPARATOR)
                append(dateText.substring(END_OF_2ND_DIGIT))
            } else {
                append(dateText)
            }
        }
    }

}