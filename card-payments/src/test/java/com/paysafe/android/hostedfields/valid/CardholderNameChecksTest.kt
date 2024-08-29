/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardholderNameChecksTest {

    @Test
    fun `IF cardholder is empty THEN input protection RETURNS empty`() {
        // Arrange
        val emptyCardholder = ""
        // Act
        val output = CardholderNameChecks.inputProtection(emptyCardholder)
        // Assert
        assertEquals(emptyCardholder, output)
    }

    @Test
    fun `IF cardholder is with whitespace THEN input protection RETURNS empty`() {
        // Arrange
        val spacesCardholder = " "
        // Act
        val output = CardholderNameChecks.inputProtection(spacesCardholder)
        // Assert
        assertEquals("", output)
    }

    @Test
    fun `IF cardholder is number THEN input protection RETURNS empty`() {
        // Arrange
        val numberCardholder = "1"
        // Act
        val output = CardholderNameChecks.inputProtection(numberCardholder)
        // Assert
        assertEquals("", output)
    }

    @Test
    fun `IF cardholder is symbol THEN input protection RETURNS empty`() {
        // Arrange
        val symbolCardholder = "$"
        // Act
        val output = CardholderNameChecks.inputProtection(symbolCardholder)
        // Assert
        assertEquals("", output)
    }

    @Test
    fun `IF cardholder is a letter THEN input protection RETURNS same text`() {
        // Arrange
        val letterCardholder = "J"
        // Act
        val output = CardholderNameChecks.inputProtection(letterCardholder)
        // Assert
        assertEquals(letterCardholder, output)
    }

    @Test
    fun `IF cardholder is a name THEN input protection RETURNS same text`() {
        // Arrange
        val nameCardholder = "John"
        // Act
        val output = CardholderNameChecks.inputProtection(nameCardholder)
        // Assert
        assertEquals(nameCardholder, output)
    }

    @Test
    fun `IF cardholder has two spaces at end THEN input protection RETURNS same text but only one space at end`() {
        // Arrange
        val cardholderWith2SpacesAtEnd = "John  "
        // Act
        val output = CardholderNameChecks.inputProtection(cardholderWith2SpacesAtEnd)
        // Assert
        assertEquals("John ", output)
    }

    @Test
    fun `IF cardholder is name lastname THEN input protection RETURNS same text`() {
        // Arrange
        val nameLastNameCardholder = "John Doe"
        // Act
        val output = CardholderNameChecks.inputProtection(nameLastNameCardholder)
        // Assert
        assertEquals(nameLastNameCardholder, output)
    }

    @Test
    fun `IF cardholder has a space at the end THEN input protection RETURNS same text`() {
        // Arrange
        val cardholderWithSpaceAtEnd = "John Doe "
        // Act
        val output = CardholderNameChecks.inputProtection(cardholderWithSpaceAtEnd)
        // Assert
        assertEquals(cardholderWithSpaceAtEnd, output)
    }

    @Test
    fun `IF cardholder is alphanumeric THEN input protection RETURNS text with only words and spaces`() {
        // Arrange
        val alphanumericCardholder = "John1 Doe2"
        // Act
        val output = CardholderNameChecks.inputProtection(alphanumericCardholder)
        // Assert
        assertEquals("John Doe", output)
    }

    @Test
    fun `IF cardholder is empty THEN validations RETURNS false`() {
        // Arrange
        val cardholderNameInput = ""
        // Act
        val notValidOutput = CardholderNameChecks.validations(cardholderNameInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is blank THEN validations RETURNS false`() {
        // Arrange
        val cardholderNameInput = "       "
        // Act
        val notValidOutput = CardholderNameChecks.validations(cardholderNameInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is alphanumeric THEN validations RETURNS false`() {
        // Arrange
        val cardholderNameInput = "John1 Doe2"
        // Act
        val notValidOutput = CardholderNameChecks.validations(cardholderNameInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is longer than 24 chars THEN validations RETURNS false`() {
        // Arrange
        val cardholderNameInput = "JohnDoe Twenty Five Chars"
        // Act
        val notValidOutput = CardholderNameChecks.validations(cardholderNameInput)
        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is lowercase and before chars limit THEN validations RETURNS true`() {
        // Arrange
        val cardholderNameInput = "johndoe in lowercase"
        // Act
        val validOutput = CardholderNameChecks.validations(cardholderNameInput)
        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder is uppercase and before chars limit THEN validations RETURNS true`() {
        // Arrange
        val cardholderNameInput = "JOHN DOE IN UPPERCASE"
        // Act
        val validOutput = CardholderNameChecks.validations(cardholderNameInput)
        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder is 24 chars long THEN validations RETURNS true`() {
        // Arrange
        val cardholderNameInput = "JohnDoe Twenty FourChars"
        // Act
        val validOutput = CardholderNameChecks.validations(cardholderNameInput)
        // Assert
        assertTrue(validOutput)
    }

}