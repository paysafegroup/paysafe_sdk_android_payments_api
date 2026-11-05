package com.paysafe.android.venmo.activity

import android.content.Intent
import com.paysafe.android.venmo.domain.model.BraintreeDetailsConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VenmoWebCheckoutActivityResultParserTest {

    private val parser = VenmoWebCheckoutActivityResultParser()

    private val details = BraintreeDetailsConfiguration(
        account = "nonce",
        firstName = "Ada",
        lastName = "Lovelace",
        phoneNumber = "555",
        email = "ada@example.com",
        userName = "venmo-user",
        externalId = "external-id",
        deviceDataCorrelationId = "corr-1",
        sessionToken = "session-token"
    )

    @Test
    fun `IF result is success THEN the Braintree details are returned`() {
        val intent = Intent().putExtra(VenmoConstants.VENMO_DATA_EXTRA, details)

        val result = parser.parseResult(VenmoConstants.RESULT_SUCCESS, intent)

        assertEquals(VenmoWebCheckoutActivityResult.Success(details), result)
    }

    @Test
    fun `IF success result has no Braintree details THEN parsing throws`() {
        val error = assertThrows(IllegalStateException::class.java) {
            parser.parseResult(VenmoConstants.RESULT_SUCCESS, Intent())
        }

        assertEquals("Braintree data is missing", error.message)
    }

    @Test
    fun `IF success result has no intent THEN parsing throws`() {
        val error = assertThrows(IllegalStateException::class.java) {
            parser.parseResult(VenmoConstants.RESULT_SUCCESS, null)
        }

        assertEquals("Braintree data is missing", error.message)
    }

    @Test
    fun `IF result is canceled THEN cancellation is returned`() {
        val result = parser.parseResult(
            VenmoConstants.RESULT_CANCELED,
            Intent().putExtra(VenmoConstants.VENMO_DATA_EXTRA, details)
        )

        assertEquals(VenmoWebCheckoutActivityResult.Canceled, result)
    }

    @Test
    fun `IF result is failed THEN failure is returned`() {
        val result = parser.parseResult(VenmoConstants.RESULT_FAILED, Intent())

        assertEquals(VenmoWebCheckoutActivityResult.Failure, result)
    }

    @Test
    fun `IF result code is unknown THEN failure is returned`() {
        val result = parser.parseResult(resultCode = -1, intent = null)

        assertEquals(VenmoWebCheckoutActivityResult.Failure, result)
    }
}
