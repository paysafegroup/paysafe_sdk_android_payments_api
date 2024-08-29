/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.MAX_CHARS_FOR_AMEX_CARD
import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.MAX_CHARS_FOR_CARD_NUMBERS
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberSpacesTest {

    private val sut = CardNumberSpaces

    @Test
    fun `IF cursor passes the max THEN amex transformedToOriginal RETURNS max chars for amex`() {
        // Arrange
        val input = 18

        // Act
        val output = sut.amexSpacesMapping.transformedToOriginal(input)

        // Assert
        assertEquals(MAX_CHARS_FOR_AMEX_CARD, output)
    }

    @Test
    fun `IF cursor passes the max THEN transformedToOriginal RETURNS default max chars`() {
        // Arrange
        val input = 20

        // Act
        val output = sut.defaultSpacesMapping.transformedToOriginal(input)

        // Assert
        assertEquals(MAX_CHARS_FOR_CARD_NUMBERS, output)
    }

}