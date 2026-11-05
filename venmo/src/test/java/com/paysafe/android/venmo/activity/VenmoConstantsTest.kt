package com.paysafe.android.venmo.activity

import org.junit.Assert
import org.junit.Test

class VenmoConstantsTest {

    private val venmoServiceConstants = VenmoServiceConstants()

    @Test
    fun `test VENMO_CHECKOUT_CONFIGURATION_EXTRA constant value`() {
        Assert.assertEquals(
            "VENMO_CHECKOUT_CONFIGURATION_EXTRA",
            VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA
        )
    }

    @Test
    fun `test VENMO_DATA_EXTRA constant value`() {
        Assert.assertEquals("VENMO_DATA_EXTRA", VenmoConstants.VENMO_DATA_EXTRA)
    }

    @Test
    fun `test RESULT_SUCCESS constant value`() {
        Assert.assertEquals(1_000, venmoServiceConstants.getResultSuccess())
    }

    @Test
    fun `test RESULT_FAILED constant value`() {
        Assert.assertEquals(1_001, venmoServiceConstants.getResultFailed())
    }

    @Test
    fun `test RESULT_CANCELED constant value`() {
        Assert.assertEquals(1_002, venmoServiceConstants.getResultCanceled())
    }
}