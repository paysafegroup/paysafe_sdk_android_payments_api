/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.activity

import android.content.Intent
import android.net.Uri
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.service.PSApiClient
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class PayPalWebCheckoutActivityTest {

    private val application = RuntimeEnvironment.getApplication()

    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockPayPalWebCheckoutClient: PayPalWebCheckoutClient
    private lateinit var spyActivity: PayPalWebCheckoutActivity

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockPSApiClient = mockk(relaxed = true)
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        mockPayPalWebCheckoutClient = mockk(relaxed = true)
        spyActivity = spyk()
    }

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `IF onPayPalWebCanceled without a receivedIntent THEN finishActivityWithResult is called with RESULT_CANCELED`() {
        // Arrange

        // Act
        spyActivity.onPayPalWebCanceled()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(PayPalConstants.RESULT_CANCELED)
        }
    }

    @Test
    fun `IF onPayPalWebCanceled without opType THEN finishActivityWithResult is called with RESULT_FAILED`() {
        // Arrange
        val uri = Uri.Builder()
            .build()
        val intent = Intent().setData(uri)

        // Act
        spyActivity.receivedIntent = intent
        spyActivity.onPayPalWebCanceled()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(PayPalConstants.RESULT_FAILED)
        }
    }

    @Test
    fun `IF onPayPalWebCanceled with opType cancel THEN finishActivityWithResult is called with RESULT_CANCELED`() {
        // Arrange
        val uri = Uri.Builder()
            .scheme("com.paysafe.android.paypal")
            .appendQueryParameter("opType", "cancel")
            .build()
        val intent = Intent().setData(uri)

        // Act
        spyActivity.receivedIntent = intent
        spyActivity.onPayPalWebCanceled()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(PayPalConstants.RESULT_CANCELED)
        }
    }

    @Test
    fun `IF onPayPalWebCanceled with opType payment THEN finishActivityWithResult is called with RESULT_SUCCESS`() {
        // Arrange
        val uri = Uri.Builder()
            .scheme("com.paysafe.android.paypal")
            .appendQueryParameter("opType", "payment")
            .build()
        val intent = Intent().setData(uri)

        // Act
        spyActivity.receivedIntent = intent
        spyActivity.onPayPalWebCanceled()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(PayPalConstants.RESULT_SUCCESS)
        }
    }

    @Test
    fun `IF onPayPalWebFailure THEN finishActivityWithResult is called with RESULT_FAILED`() {
        // Arrange

        // Act
        spyActivity.onPayPalWebFailure(PayPalSDKError(-1, ""))

        // Verify
        verify {
            spyActivity.finishActivityWithResult(PayPalConstants.RESULT_FAILED)
        }
    }

    @Test
    fun `IF onPayPalWebSuccess THEN finishActivityWithResult is called with RESULT_SUCCESS`() {
        // Arrange

        // Act
        spyActivity.onPayPalWebSuccess(PayPalWebCheckoutResult("", ""))

        // Verify
        verify {
            spyActivity.finishActivityWithResult(PayPalConstants.RESULT_SUCCESS)
        }
    }

    @Test
    fun `IF handleOnCreate without expected data in intent THEN finishActivityWithResult is called with RESULT_FAILED`() {
        // Arrange

        // Act
        spyActivity.handleOnCreate()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(PayPalConstants.RESULT_FAILED)
        }
    }

    @Test
    fun `IF handleOnCreate THEN finishActivityWithResult is called with RESULT_FAILED`() {
        // Arrange
        every { spyActivity.applicationContext } returns application
        val orderId = "orderId"
        val clientId = "clientId"
        val intent = Intent().apply {
            putExtra(PayPalConstants.INTENT_EXTRA_ORDER_ID, orderId)
            putExtra(PayPalConstants.INTENT_EXTRA_CLIENT_ID, clientId)
        }
        every { spyActivity.intent } returns intent

        // Act
        spyActivity.handleOnCreate()

        // Verify
        verify {
            spyActivity.providePayPalWebCheckoutClient(clientId)
        }
        assertEquals(orderId, spyActivity.orderId)
    }

    @Test
    fun `IF handleOnNewIntent with valid intent THEN receivedIntent is updated`() {
        // Arrange
        val uri = Uri.Builder()
            .scheme("com.paysafe.android.paypal")
            .appendQueryParameter("opType", "payment")
            .build()
        val intent = Intent().setData(uri)

        // Act
        spyActivity.handleOnNewIntent(intent)

        // Verify
        assertEquals(intent, spyActivity.receivedIntent)
    }

    @Test
    fun `IF handleOnNewIntent with null intent THEN receivedIntent is updated`() {
        // Arrange

        // Act
        spyActivity.handleOnNewIntent(null)

        // Verify
        assertNull(spyActivity.receivedIntent)
    }

    @Test
    fun `IF handleLifecycleObserverOnResume THEN PayPalCheckout is started and the observer is removed`() {
        // Arrange
        spyActivity.payPalWebCheckoutClient = mockPayPalWebCheckoutClient
        spyActivity.orderId = "orderId"

        // Act
        spyActivity.handleLifecycleObserverOnResume()

        // Verify
        verify {
            spyActivity.startPayPalCheckout()
            spyActivity.removeLifecycleObserver()
        }
    }

    @Test
    fun `IF providePayPalWebCheckoutClient THEN PayPalWebCheckoutClient is returned`() {
        // Arrange
        every { spyActivity.applicationContext } returns application

        // Act
        val result = spyActivity.providePayPalWebCheckoutClient("clientId")

        // Verify
        assertNotNull(result)
    }

    @Test
    fun `IF startPayPalCheckout THEN PayPalWebCheckoutClient start is called`() {
        // Arrange
        spyActivity.payPalWebCheckoutClient = mockPayPalWebCheckoutClient
        spyActivity.orderId = "orderId"

        // Act
        spyActivity.startPayPalCheckout()

        // Verify
        verify {
            mockPayPalWebCheckoutClient.start(any())
        }
    }
}