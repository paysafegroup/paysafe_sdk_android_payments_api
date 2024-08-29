/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExpiryDateChecksTest {

    private fun createCalendar(month: Int, year: Int): Calendar {
        val output = Calendar.getInstance()
        output.set(Calendar.YEAR, year)
        output.set(Calendar.MONTH, month - 1)
        return output
    }

    @Test
    fun `IF new expiry date is empty THEN input protection RETURNS empty output`() {
        // Arrange
        val newDateInput = ""
        val expectedOutput = ""
        // Act
        val emptyOutput = ExpiryDateChecks.inputProtection(newDateInput)
        // Assert
        assertEquals(expectedOutput, emptyOutput)
    }

    @Test
    fun `IF new expiry date is non-numeric THEN input protection RETURNS empty output`() {
        // Arrange
        val newDateInput = "W.#d"
        val expectedOutput = ""
        // Act
        val emptyOutput = ExpiryDateChecks.inputProtection(newDateInput)
        // Assert
        assertEquals(expectedOutput, emptyOutput)
    }

    @Test
    fun `IF new expiry date is over 4 digits THEN input protection RETURNS first 4 digits`() {
        // Arrange
        val newDateInput = "12345"
        val expectedOutput = "1234"
        // Act
        val fourCharsOutput = ExpiryDateChecks.inputProtection(newDateInput)
        // Assert
        assertEquals(expectedOutput, fourCharsOutput)
    }

    @Test
    fun `IF expiry date is empty THEN validations RETURNS false`() {
        // Arrange
        val expiryDateInput = ""
        // Act
        val notValidOutput = ExpiryDateChecks.validations(expiryDateInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF expiry date is blank THEN validations RETURNS false`() {
        // Arrange
        val expiryDateInput = "       "
        // Act
        val notValidOutput = ExpiryDateChecks.validations(expiryDateInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF expiry date is before current date THEN validations RETURNS false`() {
        // Arrange
        val beforeExpiryDateUserInput = "0122"
        val currentCalendarInput = createCalendar(8, 2023)
        // Act
        val notValidOutput = ExpiryDateChecks.validations(
            beforeExpiryDateUserInput, currentCalendarInput
        )
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF expiry date is equal current date THEN validations RETURNS true`() {
        // Arrange
        val beforeExpiryDateUserInput = "0923"
        val currentCalendarInput = createCalendar(8, 2023)
        // Act
        val validOutput = ExpiryDateChecks.validations(
            beforeExpiryDateUserInput, currentCalendarInput
        )
        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF expiry date is after current date THEN validations RETURNS true`() {
        // Arrange
        val beforeExpiryDateUserInput = "1025"
        val currentCalendarInput = createCalendar(10, 2024)
        // Act
        val validOutput = ExpiryDateChecks.validations(
            beforeExpiryDateUserInput, currentCalendarInput
        )
        // Assert
        assertTrue(validOutput)
    }

}