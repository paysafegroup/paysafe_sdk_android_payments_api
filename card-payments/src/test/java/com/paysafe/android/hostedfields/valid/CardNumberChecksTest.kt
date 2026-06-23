/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.hostedfields.domain.model.PSCardFieldInputEvent
import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.CC_PLACEHOLDER_FOR_15
import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.CC_PLACEHOLDER_FOR_16
import com.paysafe.android.hostedfields.valid.CardNumberChecks.Companion.MAX_CHARS_FOR_CARD_NUMBERS
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardNumberChecksTest {

    @Suppress("unused")
    private val useConstructor = CardNumberChecks()

    private val unknownCardType = PSCreditCardType.UNKNOWN

    @Test
    fun `WHEN input is empty THEN inputProtection returns unknown type and 16-digit placeholder`() {
        val output = CardNumberChecks.inputProtection("", unknownCardType)

        assertEquals("", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input contains non-digits THEN inputProtection strips invalid chars and returns unknown type`() {
        val output = CardNumberChecks.inputProtection("NotNumbers", unknownCardType)

        assertEquals("", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input contains symbols THEN inputProtection strips invalid chars and returns unknown type`() {
        val output = CardNumberChecks.inputProtection("S%e[!@|_Ch*r$", unknownCardType)

        assertEquals("", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input contains invalid characters THEN inputProtection invokes invalid character event`() {
        val events = mutableListOf<PSCardFieldInputEvent>()
        CardNumberChecks.inputProtection("4a5", unknownCardType) { events.add(it) }

        assertEquals(listOf(PSCardFieldInputEvent.INVALID_CHARACTER), events)
    }

    @Test
    fun `WHEN input does not match any brand THEN inputProtection returns unknown type`() {
        val output = CardNumberChecks.inputProtection("7", unknownCardType)

        assertEquals("7", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input matches visa prefix THEN inputProtection returns visa type and 16-digit placeholder`() {
        val output = CardNumberChecks.inputProtection("4", unknownCardType)

        assertEquals("4", output.first)
        assertEquals(PSCreditCardType.VISA, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input is single five THEN inputProtection returns unknown type`() {
        val output = CardNumberChecks.inputProtection("5", unknownCardType)

        assertEquals("5", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input matches mastercard 51-55 prefix THEN inputProtection returns mastercard type`() {
        val output = CardNumberChecks.inputProtection("51", unknownCardType)

        assertEquals("51", output.first)
        assertEquals(PSCreditCardType.MASTERCARD, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input matches mastercard 2-series prefix THEN inputProtection returns mastercard type`() {
        val output24 = CardNumberChecks.inputProtection("24", unknownCardType)
        val output2222 = CardNumberChecks.inputProtection("2222", unknownCardType)

        assertEquals(PSCreditCardType.MASTERCARD, output24.second)
        assertEquals(PSCreditCardType.MASTERCARD, output2222.second)
    }

    @Test
    fun `WHEN input is single six THEN inputProtection returns unknown type`() {
        val output = CardNumberChecks.inputProtection("6", unknownCardType)

        assertEquals("6", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input matches discover prefix THEN inputProtection returns discover type`() {
        val output644 = CardNumberChecks.inputProtection("644", unknownCardType)
        val output60112 = CardNumberChecks.inputProtection("60112", unknownCardType)

        assertEquals(PSCreditCardType.DISCOVER, output644.second)
        assertEquals(PSCreditCardType.DISCOVER, output60112.second)
    }

    @Test
    fun `WHEN input starts with three THEN inputProtection returns unknown type until amex prefix is complete`() {
        val output = CardNumberChecks.inputProtection("3", unknownCardType)

        assertEquals("3", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN input matches amex prefix THEN inputProtection returns amex type and 15-digit placeholder`() {
        val output34 = CardNumberChecks.inputProtection("34", unknownCardType)
        val output37 = CardNumberChecks.inputProtection("37", unknownCardType)

        assertEquals(PSCreditCardType.AMEX, output34.second)
        assertEquals(CC_PLACEHOLDER_FOR_15, output34.third)
        assertEquals(PSCreditCardType.AMEX, output37.second)
        assertEquals(CC_PLACEHOLDER_FOR_15, output37.third)
    }

    @Test
    fun `WHEN input starts with three but second digit is invalid THEN inputProtection returns unknown type`() {
        val output = CardNumberChecks.inputProtection("35", unknownCardType)

        assertEquals("35", output.first)
        assertEquals(PSCreditCardType.UNKNOWN, output.second)
        assertEquals(CC_PLACEHOLDER_FOR_16, output.third)
    }

    @Test
    fun `WHEN current card type is amex THEN inputProtection truncates input to amex max length`() {
        val output = CardNumberChecks.inputProtection(
            newCardNumber = "3411111111111119999",
            newCardType = PSCreditCardType.AMEX
        )

        assertEquals("341111111111111", output.first)
        assertEquals(PSCreditCardType.AMEX, output.second)
    }

    @Test
    fun `WHEN current card type is unknown THEN inputProtection truncates input to default max length`() {
        val output = CardNumberChecks.inputProtection(
            newCardNumber = "41111111111111119999",
            newCardType = unknownCardType
        )

        assertEquals("4111111111111111", output.first)
        assertEquals(MAX_CHARS_FOR_CARD_NUMBERS, output.first.length)
    }

    @Test
    fun `WHEN card number is empty THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations(""))
    }

    @Test
    fun `WHEN card number is whitespace THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("      "))
    }

    @Test
    fun `WHEN card number has two digits or fewer THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("4"))
        assertFalse(CardNumberChecks.validations("51"))
    }

    @Test
    fun `WHEN card number has less than required length THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("12345678901234"))
    }

    @Test
    fun `WHEN card number has more than max length THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("12345678901234567"))
    }

    @Test
    fun `WHEN card number is valid amex THEN validations returns true`() {
        assertTrue(CardNumberChecks.validations("341111111111111"))
        assertTrue(CardNumberChecks.validations("378282246310005"))
    }

    @Test
    fun `WHEN card number looks like amex but fails luhn THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("341111111111112"))
    }

    @Test
    fun `WHEN card number is valid visa THEN validations returns true`() {
        assertTrue(CardNumberChecks.validations("4111111111111111"))
    }

    @Test
    fun `WHEN card number looks like visa but fails luhn THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("4111111111111119"))
    }

    @Test
    fun `WHEN card number is valid mastercard THEN validations returns true`() {
        assertTrue(CardNumberChecks.validations("5105105105105100"))
        assertTrue(CardNumberChecks.validations("2223000048400011"))
    }

    @Test
    fun `WHEN card number looks like mastercard but fails luhn THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("5105105105105108"))
    }

    @Test
    fun `WHEN card number is valid discover THEN validations returns true`() {
        assertTrue(CardNumberChecks.validations("6011000990139424"))
    }

    @Test
    fun `WHEN card number looks like discover but fails luhn THEN validations returns false`() {
        assertFalse(CardNumberChecks.validations("6011000990139425"))
    }
}
