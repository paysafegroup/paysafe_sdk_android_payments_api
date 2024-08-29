/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import com.paysafe.android.hostedfields.valid.ExpiryDateChecks.Companion.MONTH_YEAR_SEPARATOR
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpiryDateSlashTest {

    private val sut = ExpiryDateSlash

    @Test
    fun `IF cursor passes the max THEN slash transformedToOriginal RETURNS input minus separator length`() {
        // Arrange
        val input = 7

        // Act
        val output = sut.slashMapping.transformedToOriginal(input)

        // Assert
        assertEquals(input - MONTH_YEAR_SEPARATOR.length, output)
    }

}