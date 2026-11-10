/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardholderNameChecksTest {

    @Suppress("unused")
    private val useConstructor = CardholderNameChecks()

    @Test
    fun `IF cardholder is empty THEN input protection RETURNS empty and reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("") { events.add(it) }

        // Assert
        assertEquals("", output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF cardholder is only spaces THEN input protection RETURNS empty without invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("   ") { events.add(it) }

        // Assert
        assertEquals("", output)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `IF cardholder has leading spaces THEN input protection REMOVES them`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("  John Doe") { events.add(it) }

        // Assert
        assertEquals("John Doe", output)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `IF cardholder has trailing spaces THEN input protection KEEPS them`() {
        // Arrange
        val cardholderWithSpacesAtEnd = "John Doe  "

        // Act
        val output = CardholderNameChecks.inputProtection(cardholderWithSpacesAtEnd)

        // Assert
        assertEquals(cardholderWithSpacesAtEnd, output)
    }

    @Test
    fun `IF cardholder has leading and trailing spaces THEN input protection TRIMS only the start`() {
        // Act
        val output = CardholderNameChecks.inputProtection("  John Doe  ")

        // Assert
        assertEquals("John Doe  ", output)
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
    fun `IF cardholder is name and lastname THEN input protection RETURNS same text`() {
        // Arrange
        val nameLastNameCardholder = "John Doe"

        // Act
        val output = CardholderNameChecks.inputProtection(nameLastNameCardholder)

        // Assert
        assertEquals(nameLastNameCardholder, output)
    }

    @Test
    fun `IF cardholder uses hyphen apostrophe or accented letters THEN input protection KEEPS them`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()
        val cardholderName = "Hélène-François O'Brien"

        // Act
        val output = CardholderNameChecks.inputProtection(cardholderName) { events.add(it) }

        // Assert
        assertEquals(cardholderName, output)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `IF cardholder uses latin1 boundary letters THEN input protection KEEPS them`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("Àÿ") { events.add(it) }

        // Assert
        assertEquals("Àÿ", output)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `IF cardholder is a number THEN input protection KEEPS it and reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("1") { events.add(it) }

        // Assert
        assertEquals("1", output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF cardholder is a symbol THEN input protection KEEPS it and reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("$") { events.add(it) }

        // Assert
        assertEquals("$", output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF cardholder is alphanumeric THEN input protection KEEPS it and reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()
        val alphanumericCardholder = "John1 Doe2"

        // Act
        val output = CardholderNameChecks.inputProtection(alphanumericCardholder) { events.add(it) }

        // Assert
        assertEquals(alphanumericCardholder, output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF cardholder has a letter outside latin1 THEN input protection KEEPS it and reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("Ā") { events.add(it) }

        // Assert
        assertEquals("Ā", output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF cardholder has leading tab THEN input protection TRIMS it and reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("\tJohn") { events.add(it) }

        // Assert
        assertEquals("John", output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF cardholder has trailing tab THEN input protection KEEPS it and reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("John\t") { events.add(it) }

        // Assert
        assertEquals("John\t", output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF invalid cardholder has no event callback THEN input protection does not fail`() {
        // Act
        val output = CardholderNameChecks.inputProtection("1")

        // Assert
        assertEquals("1", output)
    }

    @Test
    fun `IF cardholder is longer than 24 chars THEN input protection RETURNS first 24 chars`() {
        // Arrange
        val cardholderName = "A".repeat(30)

        // Act
        val output = CardholderNameChecks.inputProtection(cardholderName)

        // Assert
        assertEquals("A".repeat(24), output)
    }

    @Test
    fun `IF cardholder is exactly 24 chars THEN input protection RETURNS same text`() {
        // Arrange
        val cardholderName = "A".repeat(24)
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection(cardholderName) { events.add(it) }

        // Assert
        assertEquals(cardholderName, output)
        assertTrue(events.isEmpty())
    }

    @Test
    fun `IF cardholder starts with space and exceeds 24 chars THEN input protection TRUNCATES before trim`() {
        // Arrange
        val cardholderName = " " + "A".repeat(24)

        // Act
        val output = CardholderNameChecks.inputProtection(cardholderName)

        // Assert
        assertEquals("A".repeat(23), output)
    }

    @Test
    fun `IF invalid character is past 24 chars THEN input protection TRUNCATES it and still reports invalid character`() {
        // Arrange
        val events = mutableListOf<PSCardFieldInputEvent>()

        // Act
        val output = CardholderNameChecks.inputProtection("A".repeat(24) + "1") { events.add(it) }

        // Assert
        assertEquals("A".repeat(24), output)
        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `IF cardholder is empty THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("")

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is blank THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("       ")

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is a single letter THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("J")

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is two letters THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("Jo")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder is alphanumeric THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("John1 Doe2")

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder contains a symbol THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("John$")

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder contains a tab THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("John\tDoe")

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is longer than 24 chars THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("JohnDoe Twenty Five Chars")

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is 25 allowed chars THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("A".repeat(25))

        // Assert
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is 24 chars with a trailing space THEN validations RETURNS false`() {
        // Arrange
        val cardholderNameInput = "JohnDoe Twenty FourChars "

        // Act
        val notValidOutput = CardholderNameChecks.validations(cardholderNameInput)

        // Assert
        assertEquals(25, cardholderNameInput.length)
        assertFalse(notValidOutput)
    }

    @Test
    fun `IF cardholder is lowercase and before chars limit THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("johndoe in lowercase")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder is uppercase and before chars limit THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("JOHN DOE IN UPPERCASE")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder is 24 chars long THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("JohnDoe Twenty FourChars")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder has a trailing space THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("John Doe ")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder has a leading space THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations(" John")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder has repeated internal spaces THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("John  Doe")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder uses hyphen apostrophe or accented letters THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("Hélène O'Brien")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder uses latin1 boundary letters THEN validations RETURNS true`() {
        // Act
        val validOutput = CardholderNameChecks.validations("Àÿ")

        // Assert
        assertTrue(validOutput)
    }

    @Test
    fun `IF cardholder uses a letter outside latin1 THEN validations RETURNS false`() {
        // Act
        val notValidOutput = CardholderNameChecks.validations("Āna")

        // Assert
        assertFalse(notValidOutput)
    }
}
