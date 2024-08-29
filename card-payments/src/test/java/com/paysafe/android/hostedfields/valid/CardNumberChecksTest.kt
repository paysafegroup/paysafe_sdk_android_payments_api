/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.CC_PLACEHOLDER_FOR_15
import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.CC_PLACEHOLDER_FOR_16
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import org.junit.Assert.assertEquals
import org.junit.Test

class CardNumberChecksTest {

    @Suppress("unused")
    private val useConstructor = CardNumberChecks()

    private val isValid = true
    private val isNotValid = false
    private val notRelevantCardTypeInput = PSCreditCardType.UNKNOWN

    @Test
    fun `IF card number^type are empty^unknown THEN input protection RETURNS same input and 4 groups(16x) placeholder`() {
        // Arrange
        val charsTypedOneByOne = ""
        val cardTypeInput = PSCreditCardType.UNKNOWN
        // Act
        val output = CardNumberChecks.inputProtection(charsTypedOneByOne, cardTypeInput)
        // Assert
        assertEquals(charsTypedOneByOne, output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF card numbers are letters THEN input protection RETURNS empty and 4 groups(16x) placeholder`() {
        // Arrange
        val inputCharsTypedOneByOne = "NotNumbers"
        // Act
        val output = CardNumberChecks.inputProtection(
            inputCharsTypedOneByOne, notRelevantCardTypeInput
        )
        // Assert
        assertEquals("", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF card numbers aren't digits THEN input protection RETURNS empty and 4 groups(16x) placeholder`() {
        // Arrange
        val inputCharsTypedOneByOne = "S%e[!@|_Ch*r$"
        // Act
        val output = CardNumberChecks.inputProtection(
            inputCharsTypedOneByOne, notRelevantCardTypeInput
        )
        // Assert
        assertEquals("", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF single number of unknown type THEN input protection RETURNS same number and 4 groups(16x) placeholder`() {
        // Arrange
        val singleNumberAsText = "7"
        // Act
        val output = CardNumberChecks.inputProtection(
            singleNumberAsText, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(singleNumberAsText, output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF start number is for VISA THEN input protection RETURNS same number with VISA type and 4 groups(16x) placeholder`() {
        // Arrange
        val startNumberForVisa = "4"
        // Act
        val output = CardNumberChecks.inputProtection(
            startNumberForVisa, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(startNumberForVisa, output.first)
        assertEquals(PSCreditCardType.VISA, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF start number is for MASTERCARD THEN input protection RETURNS same number with MASTERCARD type and 4 groups(16x) placeholder`() {
        // Arrange
        val startNumberForMastercard = "5"
        // Act
        val output = CardNumberChecks.inputProtection(
            startNumberForMastercard, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(startNumberForMastercard, output.first)
        assertEquals(PSCreditCardType.MASTERCARD, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF start number is for DISCOVER THEN input protection RETURNS same number with DISCOVER type and 4 groups(16x) placeholder`() {
        // Arrange
        val startNumberForDiscover = "6"
        // Act
        val output = CardNumberChecks.inputProtection(
            startNumberForDiscover, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(startNumberForDiscover, output.first)
        assertEquals(PSCreditCardType.DISCOVER, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF start number is for AMEX THEN input protection RETURNS same number with UNKNOWN type and 4 groups(16x) placeholder`() {
        // Arrange
        val startNumberForAmex = "3"
        // Act
        val output = CardNumberChecks.inputProtection(
            startNumberForAmex, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(startNumberForAmex, output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF first two numbers(1st case) are for AMEX THEN input protection RETURNS same numbers with AMEX type and 3 groups(15x) placeholder`() {
        // Arrange
        val startNumbersForAmex1stCase = "34"
        // Act
        val output = CardNumberChecks.inputProtection(
            startNumbersForAmex1stCase, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(startNumbersForAmex1stCase, output.first)
        assertEquals(PSCreditCardType.AMEX, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_15, output.third)
    }

    @Test
    fun `IF first two numbers(2nd case) are for AMEX THEN input protection RETURNS same numbers with AMEX type and 3 groups(15x) placeholder`() {
        // Arrange
        val startNumbersForAmex2ndCase = "37"
        // Act
        val output = CardNumberChecks.inputProtection(
            startNumbersForAmex2ndCase, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(startNumbersForAmex2ndCase, output.first)
        assertEquals(PSCreditCardType.AMEX, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_15, output.third)
    }

    @Test
    fun `IF 1st number is for AMEX and 2nd no THEN input protection RETURNS same numbers with UNKNOWN type and 4 groups(16x) placeholder`() {
        // Arrange
        val firstNumberForAmex2ndOneNo = "35"
        // Act
        val output = CardNumberChecks.inputProtection(
            firstNumberForAmex2ndOneNo, notRelevantCardTypeInput
        )
        // Assert
        assertEquals(firstNumberForAmex2ndOneNo, output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `IF card number is empty THEN validations RETURNS false`() {
        // Arrange
        val emptyCardNumber = ""
        // Act
        val output = CardNumberChecks.validations(emptyCardNumber)
        // Assert
        assertEquals(isNotValid, output)
    }

    @Test
    fun `IF card number is with whitespaces THEN validations RETURNS false`() {
        // Arrange
        val spacesCardNumber = "      "
        // Act
        val output = CardNumberChecks.validations(spacesCardNumber)
        // Assert
        assertEquals(isNotValid, output)
    }

    @Test
    fun `IF card number has less than 15 chars THEN validations RETURNS false`() {
        // Arrange
        val below15CardNumber = "12345678901234"
        // Act
        val output = CardNumberChecks.validations(below15CardNumber)
        // Assert
        assertEquals(isNotValid, output)
    }

    @Test
    fun `IF card number has 15 chars THEN validations RETURNS true`() {
        // Arrange
        val fifteenCharsCardNumber = "378282246310005"
        // Act
        val output = CardNumberChecks.validations(fifteenCharsCardNumber)
        // Assert
        assertEquals(isValid, output)
    }

    @Test
    fun `IF card number has more than 16 chars THEN validations RETURNS false`() {
        // Arrange
        val over16CardNumber = "12345678901234567"
        // Act
        val output = CardNumberChecks.validations(over16CardNumber)
        // Assert
        assertEquals(isNotValid, output)
    }

    @Test
    fun `IF card number is a valid AMEX THEN validations RETURNS true`() {
        // Arrange
        val amexCardNumber = "341111111111111"
        // Act
        val output = CardNumberChecks.validations(amexCardNumber)
        // Assert
        assertEquals(isValid, output)
    }

    @Test
    fun `IF card number begins with AMEX digits but is not valid THEN validations RETURNS false`() {
        // Arrange
        val almostAnAmexCard = "341111111111112"
        // Act
        val output = CardNumberChecks.validations(almostAnAmexCard)
        // Assert
        assertEquals(isNotValid, output)
    }

    @Test
    fun `IF card number is a valid VISA THEN validations RETURNS true`() {
        // Arrange
        val visaCardNumber = "4111111111111111"
        // Act
        val output = CardNumberChecks.validations(visaCardNumber)
        // Assert
        assertEquals(isValid, output)
    }

    @Test
    fun `IF card number begins with VISA digits but is not valid THEN validations RETURNS false`() {
        // Arrange
        val almostVisaCard = "4111111111111119"
        // Act
        val output = CardNumberChecks.validations(almostVisaCard)
        // Assert
        assertEquals(isNotValid, output)
    }

    @Test
    fun `IF card number is a valid MASTERCARD THEN validations RETURNS true`() {
        // Arrange
        val masterCardNumber = "5105105105105100"
        // Act
        val output = CardNumberChecks.validations(masterCardNumber)
        // Assert
        assertEquals(isValid, output)
    }

    @Test
    fun `IF card number begins with MASTERCARD digits but is not valid THEN validations RETURNS false`() {
        // Arrange
        val almostMasterCard = "5105105105105108"
        // Act
        val output = CardNumberChecks.validations(almostMasterCard)
        // Assert
        assertEquals(isNotValid, output)
    }

    @Test
    fun `IF card number is a valid DISCOVER THEN validations RETURNS true`() {
        // Arrange
        val discoverCardNumber = "6011000990139424"
        // Act
        val output = CardNumberChecks.validations(discoverCardNumber)
        // Assert
        assertEquals(isValid, output)
    }

    @Test
    fun `IF card number begins with DISCOVER digits but is not valid THEN validations RETURNS false`() {
        // Arrange
        val almostDiscoverCard = "6011000990139425"
        // Act
        val output = CardNumberChecks.validations(almostDiscoverCard)
        // Assert
        assertEquals(isNotValid, output)
    }

}