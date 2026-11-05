package com.paysafe.android.venmo.data

import android.content.Context
import com.braintreepayments.api.datacollector.DataCollector
import com.braintreepayments.api.datacollector.DataCollectorCallback
import com.braintreepayments.api.datacollector.DataCollectorRequest
import com.braintreepayments.api.datacollector.DataCollectorResult
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class BraintreeDeviceDataCollectorTest {

    private val context = mockk<Context>()
    private val authorization = "client-token"
    private val dataCollector = mockk<DataCollector>()
    private val callbackSlot = slot<DataCollectorCallback>()

    private var factoryContext: Context? = null
    private var factoryAuthorization: String? = null

    private val collector = BraintreeDeviceDataCollector { receivedContext, receivedAuthorization ->
        factoryContext = receivedContext
        factoryAuthorization = receivedAuthorization
        dataCollector
    }

    @Test
    fun `IF collection succeeds THEN device data is returned without location consent`() {
        val deviceData = """{"correlation_id":"corr-1"}"""
        val clientMetadataId = "meta-123"
        val success = mockk<DataCollectorResult.Success>()
        every { success.deviceData } returns deviceData
        every {
            dataCollector.collectDeviceDataOnSuccess(any(), any(), capture(callbackSlot))
        } just Runs
        var collected: Result<String>? = null

        collector.collect(context, authorization, clientMetadataId) { collected = it }
        callbackSlot.captured.onDataCollectorResult(success)

        assertEquals(Result.success(deviceData), collected)
        assertSame(context, factoryContext)
        assertEquals(authorization, factoryAuthorization)
        verify(exactly = 1) {
            dataCollector.collectDeviceDataOnSuccess(
                context,
                DataCollectorRequest(
                    hasUserLocationConsent = false,
                    riskCorrelationId = clientMetadataId
                ),
                any()
            )
        }
    }

    @Test
    fun `IF client metadata id is missing THEN the request has no risk correlation id`() {
        val deviceData = "{}"
        val success = mockk<DataCollectorResult.Success>()
        every { success.deviceData } returns deviceData
        every {
            dataCollector.collectDeviceDataOnSuccess(any(), any(), capture(callbackSlot))
        } just Runs
        var collected: Result<String>? = null

        collector.collect(context, authorization, clientMetadataId = null) { collected = it }
        callbackSlot.captured.onDataCollectorResult(success)

        assertEquals(Result.success(deviceData), collected)
        verify(exactly = 1) {
            dataCollector.collectDeviceDataOnSuccess(
                context,
                DataCollectorRequest(
                    hasUserLocationConsent = false,
                    riskCorrelationId = null
                ),
                any()
            )
        }
    }

    @Test
    fun `IF collection fails THEN the Braintree error is returned`() {
        val error = IllegalStateException("collection failed")
        val failure = mockk<DataCollectorResult.Failure>()
        every { failure.error } returns error
        every {
            dataCollector.collectDeviceDataOnSuccess(any(), any(), capture(callbackSlot))
        } just Runs
        var collected: Result<String>? = null

        collector.collect(context, authorization, "meta-123") { collected = it }
        callbackSlot.captured.onDataCollectorResult(failure)

        assertEquals(Result.failure<String>(error), collected)
        verify(exactly = 1) {
            dataCollector.collectDeviceDataOnSuccess(
                context,
                DataCollectorRequest(
                    hasUserLocationConsent = false,
                    riskCorrelationId = "meta-123"
                ),
                any()
            )
        }
    }
}
