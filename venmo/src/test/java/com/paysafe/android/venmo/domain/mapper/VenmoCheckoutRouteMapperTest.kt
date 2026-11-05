package com.paysafe.android.venmo.domain.mapper

import com.paysafe.android.venmo.domain.model.VenmoCheckoutRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VenmoCheckoutRouteMapperTest {

    private val mapper = VenmoCheckoutRouteMapper()

    @Test
    fun `IF status is PAYABLE THEN route returns Payable`() {
        val route = decide(status = "PAYABLE", brainTreeCustomerId = "customer-1")

        assertTrue(route is VenmoCheckoutRoute.Payable)
    }

    @Test
    fun `IF status is FAILED THEN route fails`() {
        val route = decide(status = "FAILED")

        assertEquals("FAILED", (route as VenmoCheckoutRoute.Failed).status)
    }

    @Test
    fun `IF status is EXPIRED THEN route fails`() {
        val route = decide(status = "EXPIRED")

        assertEquals("EXPIRED", (route as VenmoCheckoutRoute.Failed).status)
    }

    @Test
    fun `IF session token is missing THEN route fails`() {
        val route = decide(sessionToken = null)

        assertEquals(
            "Missing sessionToken or clientToken",
            (route as VenmoCheckoutRoute.Failed).status
        )
    }

    @Test
    fun `IF client token is missing THEN route fails`() {
        val route = decide(clientToken = null)

        assertEquals(
            "Missing sessionToken or clientToken",
            (route as VenmoCheckoutRoute.Failed).status
        )
    }

    @Test
    fun `IF recurring shopper THEN route is Recurring with client metadata id`() {
        val route = decide(
            brainTreeCustomerId = "customer-1",
            clientMetadataId = "meta-1"
        )

        val recurring = route as VenmoCheckoutRoute.Recurring
        assertEquals("session-token", recurring.sessionToken)
        assertEquals("client-token", recurring.clientToken)
        assertEquals("meta-1", recurring.clientMetadataId)
    }

    @Test
    fun `IF recurring shopper has blank customer id THEN first time route is used`() {
        val route = decide(brainTreeCustomerId = "  ", clientMetadataId = "meta-1")

        assertTrue(route is VenmoCheckoutRoute.FirstTime)
    }

    @Test
    fun `IF first time redirect THEN route is FirstTime with client metadata id`() {
        val route = decide(clientMetadataId = "meta-1")

        val firstTime = route as VenmoCheckoutRoute.FirstTime
        assertEquals("session-token", firstTime.sessionToken)
        assertEquals("client-token", firstTime.clientToken)
        assertEquals("meta-1", firstTime.clientMetadataId)
    }

    @Test
    fun `IF first time redirect has no client metadata id THEN it is null`() {
        val route = decide(clientMetadataId = null)

        assertNull((route as VenmoCheckoutRoute.FirstTime).clientMetadataId)
    }

    @Test
    fun `IF action is not REDIRECT THEN Venmo is not opened`() {
        val route = decide(action = "NONE")

        assertTrue(route is VenmoCheckoutRoute.NoVenmoCheckout)
    }

    @Test
    fun `IF recurring shopper without REDIRECT THEN Venmo is not opened`() {
        val route = decide(
            action = "NONE",
            brainTreeCustomerId = "customer-1"
        )

        assertTrue(route is VenmoCheckoutRoute.NoVenmoCheckout)
    }

    @Test
    fun `IF status is COMPLETED THEN Venmo is not opened`() {
        val route = decide(status = "COMPLETED", action = "REDIRECT")

        assertTrue(route is VenmoCheckoutRoute.NoVenmoCheckout)
    }

    @Test
    fun `IF status is INITIATED with REDIRECT THEN first time route is used`() {
        val route = decide(status = "INITIATED", action = "REDIRECT")

        assertTrue(route is VenmoCheckoutRoute.FirstTime)
    }

    private fun decide(
        status: String? = "PROCESSING",
        action: String? = "REDIRECT",
        brainTreeCustomerId: String? = null,
        clientToken: String? = "client-token",
        sessionToken: String? = "session-token",
        clientMetadataId: String? = null
    ) = mapper(
        status = status,
        action = action,
        brainTreeCustomerId = brainTreeCustomerId,
        clientToken = clientToken,
        sessionToken = sessionToken,
        clientMetadataId = clientMetadataId
    )
}
