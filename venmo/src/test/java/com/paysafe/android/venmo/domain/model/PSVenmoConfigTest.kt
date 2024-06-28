package com.paysafe.android.venmo.domain.model

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PSVenmoConfigTest {

    @Test
    fun `test PSVenmoConfig creation`() {
        // Arrange
        val currencyCode = "USD"
        val accountId = "12345"

        // Act
        val config = PSVenmoConfig(currencyCode, accountId)

        // Assert
        assertNotNull(config)
        assertEquals(currencyCode, config.currencyCode)
        assertEquals(accountId, config.accountId)
    }

    @Test
    fun `test PSVenmoConfig copy`() {
        // Arrange
        val original = PSVenmoConfig("USD", "12345")

        // Act
        val copy = original.copy(accountId = "67890")

        // Assert
        assertNotNull(copy)
        assertEquals("USD", copy.currencyCode)
        assertEquals("67890", copy.accountId)
    }

    @Test
    fun `test PSVenmoConfig equals and hashCode`() {
        // Arrange
        val config1 = PSVenmoConfig("USD", "12345")
        val config2 = PSVenmoConfig("USD", "12345")
        val config3 = PSVenmoConfig("EUR", "67890")

        // Assert
        assertEquals(config1, config2)
        assertNotEquals(config1, config3)
        assertEquals(config1.hashCode(), config2.hashCode())
        assertNotEquals(config1.hashCode(), config3.hashCode())
    }
    @Test
    fun `test PSVenmoConfig toString`() {
        // Arrange
        val config = PSVenmoConfig("USD", "12345")

        // Act
        val toStringOutput = config.toString()

        // Assert
        assertTrue(toStringOutput.contains("currencyCode=USD"))
        assertTrue(toStringOutput.contains("accountId=12345"))
    }
}
