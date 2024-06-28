package com.paysafe.android.venmo.activity

import org.junit.Assert
import org.junit.Test

class VenmoConstantsTest {

    private val venmoServiceConstants = VenmoServiceConstants()

    @Test
    fun `test INTENT_EXTRA_SESSION_TOKEN constant value`() {
        Assert.assertEquals("SESSION_TOKEN", venmoServiceConstants.getIntentExtraSessionToken())
    }


    @Test
    fun `test INTENT_EXTRA_CLIENT_TOKEN constant value`() {
        Assert.assertEquals("CLIENT_TOKEN", venmoServiceConstants.getIntentExtraClientToken())
    }

    @Test
    fun `test INTENT_EXTRA_CUSTOM_URL_SCHEME constant value`() {
        Assert.assertEquals("CUSTOM_URL_SCHEME", venmoServiceConstants.getIntentExtraCustomUrlSchemeToken())
    }

    @Test
    fun `test RESULT_SUCCESS constant value`() {
        Assert.assertEquals(1_000, venmoServiceConstants.getResultSuccess())
    }

    @Test
    fun `test RESULT_FAILED constant value`() {
        Assert.assertEquals(1_001, venmoServiceConstants.getResultFailed())
    }
}