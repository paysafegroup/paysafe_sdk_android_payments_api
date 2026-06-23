/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.valid

import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

class CardInfoProviderTest {

    @Test
    fun `WHEN card number is empty THEN getCardInfo returns null`() {
        assertNull("".getCardInfo())
    }

    @Test
    fun `WHEN card number does not match any pattern THEN getCardInfo returns null`() {
        assertNull("7".getCardInfo())
        assertNull("35".getCardInfo())
        assertNull("3".getCardInfo())
        assertNull("5".getCardInfo())
        assertNull("6".getCardInfo())
        assertNull("64".getCardInfo())
        assertNull("6011".getCardInfo())
    }

    @Test
    fun `WHEN card number matches visa prefix THEN getCardInfo returns visa card info`() {
        val cardInfo = "4".getCardInfo()

        assertEquals(PSCreditCardType.VISA, cardInfo?.type)
        assertEquals(16, cardInfo?.maxLength)
    }

    @Test
    fun `WHEN card number matches mastercard 51-55 prefix THEN getCardInfo returns mastercard card info`() {
        val cardInfo = "51".getCardInfo()

        assertEquals(PSCreditCardType.MASTERCARD, cardInfo?.type)
        assertEquals(16, cardInfo?.maxLength)
    }

    @Test
    fun `WHEN card number matches mastercard 2-series prefix THEN getCardInfo returns mastercard card info`() {
        assertEquals(PSCreditCardType.MASTERCARD, "24".getCardInfo()?.type)
        assertEquals(PSCreditCardType.MASTERCARD, "2222".getCardInfo()?.type)
        assertEquals(PSCreditCardType.MASTERCARD, "27200".getCardInfo()?.type)
    }

    @Test
    fun `WHEN card number matches amex prefix THEN getCardInfo returns amex card info`() {
        val cardInfo34 = "34".getCardInfo()
        val cardInfo37 = "37".getCardInfo()

        assertEquals(PSCreditCardType.AMEX, cardInfo34?.type)
        assertEquals(15, cardInfo34?.maxLength)
        assertEquals(PSCreditCardType.AMEX, cardInfo37?.type)
        assertEquals(15, cardInfo37?.maxLength)
    }

    @Test
    fun `WHEN card number matches discover prefix THEN getCardInfo returns discover card info`() {
        val cardInfo644 = "644".getCardInfo()
        val cardInfo60112 = "60112".getCardInfo()
        val fullDiscover = "6011000990139424".getCardInfo()

        assertEquals(PSCreditCardType.DISCOVER, cardInfo644?.type)
        assertEquals(16, cardInfo644?.maxLength)
        assertEquals(PSCreditCardType.DISCOVER, cardInfo60112?.type)
        assertEquals(PSCreditCardType.DISCOVER, fullDiscover?.type)
    }

    @Test
    fun `WHEN supported card type is requested THEN getCardInfo returns matching card info`() {
        assertEquals(16, PSCreditCardType.VISA.getCardInfo()?.maxLength)
        assertEquals(16, PSCreditCardType.MASTERCARD.getCardInfo()?.maxLength)
        assertEquals(15, PSCreditCardType.AMEX.getCardInfo()?.maxLength)
        assertEquals(16, PSCreditCardType.DISCOVER.getCardInfo()?.maxLength)
    }

    @Test
    fun `WHEN unsupported card type is requested THEN getCardInfo returns null`() {
        assertNull(PSCreditCardType.UNKNOWN.getCardInfo())
        assertNull(PSCreditCardType.JCB.getCardInfo())
        assertNull(PSCreditCardType.MAESTRO.getCardInfo())
        assertNull(PSCreditCardType.SOLO.getCardInfo())
        assertNull(PSCreditCardType.VISA_DEBIT.getCardInfo())
        assertNull(PSCreditCardType.VISA_ELECTRON.getCardInfo())
    }

    @RunWith(Parameterized::class)
    class StringGetCardInfoParameterizedTest(
        private val cardNumber: String,
        private val expectedType: PSCreditCardType?,
    ) {

        @Test
        fun `WHEN card number prefix is typed THEN getCardInfo resolves expected brand`() {
            assertEquals(expectedType, cardNumber.getCardInfo()?.type)
        }

        companion object {
            @JvmStatic
            @Parameterized.Parameters(name = "cardNumber={0} -> {1}")
            fun data(): Collection<Array<Any?>> = listOf(
                arrayOf("", null),
                arrayOf("4", PSCreditCardType.VISA),
                arrayOf("4111", PSCreditCardType.VISA),
                arrayOf("5", null),
                arrayOf("51", PSCreditCardType.MASTERCARD),
                arrayOf("24", PSCreditCardType.MASTERCARD),
                arrayOf("2222", PSCreditCardType.MASTERCARD),
                arrayOf("3", null),
                arrayOf("34", PSCreditCardType.AMEX),
                arrayOf("37", PSCreditCardType.AMEX),
                arrayOf("35", null),
                arrayOf("6", null),
                arrayOf("64", null),
                arrayOf("644", PSCreditCardType.DISCOVER),
                arrayOf("6011", null),
                arrayOf("60112", PSCreditCardType.DISCOVER),
            )
        }
    }
}
