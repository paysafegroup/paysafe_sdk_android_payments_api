/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CvvChecksTest {

    @Suppress("unused")
    private val useConstructor = CvvChecks()
    private val notRelevantCardTypeInput = PSCreditCardType.UNKNOWN

    @Test
    fun `IF cvv is empty THEN input protection RETURNS empty output`() {
        // Arrange
        val cvvInput = ""
        // Act
        val output = CvvChecks.inputProtection(cvvInput, notRelevantCardTypeInput)
        // Assert
        assertEquals(cvvInput, output)
    }

    @Test
    fun `IF cvv has spaces THEN input protection RETURNS empty output`() {
        // Arrange
        val cvvInput = "    "
        // Act
        val output = CvvChecks.inputProtection(cvvInput, notRelevantCardTypeInput)
        // Assert
        assertEquals("", output)
    }

    @Test
    fun `IF cvv is a number THEN input protection RETURNS same input`() {
        // Arrange
        val cvvInput = "1"
        // Act
        val output = CvvChecks.inputProtection(cvvInput, notRelevantCardTypeInput)
        // Assert
        assertEquals(cvvInput, output)
    }

    @Test
    fun `IF cvv is a letter THEN input protection RETURNS empty output`() {
        // Arrange
        val cvvInput = "H"
        // Act
        val output = CvvChecks.inputProtection(cvvInput, notRelevantCardTypeInput)
        // Assert
        assertEquals("", output)
    }

    @Test
    fun `IF cvv is a symbol THEN input protection RETURNS empty output`() {
        // Arrange
        val cvvInput = "$"
        // Act
        val output = CvvChecks.inputProtection(cvvInput, notRelevantCardTypeInput)
        // Assert
        assertEquals("", output)
    }

    @Test
    fun `IF cvv has three numbers THEN input protection RETURNS same input`() {
        // Arrange
        val cvvInput = "123"
        // Act
        val output = CvvChecks.inputProtection(cvvInput, notRelevantCardTypeInput)
        // Assert
        assertEquals(cvvInput, output)
    }

    @Test
    fun `IF cvv has more than three numbers THEN input protection RETURNS only first three digits`() {
        // Arrange
        val cvvInput = "123456789"
        // Act
        val output = CvvChecks.inputProtection(cvvInput, notRelevantCardTypeInput)
        // Assert
        assertEquals("123", output)
    }

    @Test
    fun `IF amex cvv has four numbers THEN input protection RETURNS same input`() {
        // Arrange
        val cvvInput = "1234"
        val amexTypeInput = PSCreditCardType.AMEX
        // Act
        val output = CvvChecks.inputProtection(cvvInput, amexTypeInput)
        // Assert
        assertEquals(cvvInput, output)
    }

    @Test
    fun `IF amex cvv has more than four numbers THEN input protection RETURNS only first four digits`() {
        // Arrange
        val cvvInput = "123456789"
        val amexTypeInput = PSCreditCardType.AMEX
        // Act
        val output = CvvChecks.inputProtection(cvvInput, amexTypeInput)
        // Assert
        assertEquals("1234", output)
    }

    @Test
    fun `IF cvv is empty THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = ""
        // Act
        val notValidOutput = CvvChecks.validations(cvvInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cvv is with spaces THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = "       "
        // Act
        val notValidOutput = CvvChecks.validations(cvvInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cvv is a number THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = "1"
        // Act
        val output = CvvChecks.validations(cvvInput)
        // Assert
        assertFalse(output)
    }

    @Test
    fun `IF cvv is a letter THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = "H"
        // Act
        val output = CvvChecks.validations(cvvInput)
        // Assert
        assertFalse(output)
    }

    @Test
    fun `IF cvv is alphanumeric THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = "&2E"
        // Act
        val output = CvvChecks.validations(cvvInput)
        // Assert
        assertFalse(output)
    }

    @Test
    fun `IF cvv has 3 numbers THEN validations RETURNS true`() {
        // Arrange
        val cvvInput = "123"
        // Act
        val output = CvvChecks.validations(cvvInput)
        // Assert
        assertTrue(output)
    }

    @Test
    fun `IF cvv has more than 3 numbers THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = "1234567"
        // Act
        val output = CvvChecks.validations(cvvInput)
        // Assert
        assertFalse(output)
    }

    @Test
    fun `IF amex cvv has 4 numbers THEN validations RETURNS true`() {
        // Arrange
        val cvvInput = "1234"
        val amexTypeInput = PSCreditCardType.AMEX
        // Act
        val output = CvvChecks.validations(cvvInput, amexTypeInput)
        // Assert
        assertTrue(output)
    }

    @Test
    fun `IF amex cvv has less than 4 numbers THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = "123"
        val amexTypeInput = PSCreditCardType.AMEX
        // Act
        val output = CvvChecks.validations(cvvInput, amexTypeInput)
        // Assert
        assertFalse(output)
    }

    @Test
    fun `IF amex cvv has more than 4 numbers THEN validations RETURNS false`() {
        // Arrange
        val cvvInput = "12345678"
        val amexTypeInput = PSCreditCardType.AMEX
        // Act
        val output = CvvChecks.validations(cvvInput, amexTypeInput)
        // Assert
        assertFalse(output)
    }

}