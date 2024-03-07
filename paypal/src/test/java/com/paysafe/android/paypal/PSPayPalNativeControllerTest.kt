/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.lifecycleScope
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.paypal.exception.payPalFailedAuthorizationException
import com.paysafe.android.paypal.exception.payPalUserCancelledException
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSPayPalNativeControllerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val correlationId = "testCorrelationId"
    private val orderId = "orderId"

    private lateinit var mockActivity: AppCompatActivity
    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockPSTokenizationService: PSTokenizationService
    private lateinit var mockPayPalNativeCheckoutClient: PayPalNativeCheckoutClient
    private lateinit var mockPSPayPalTokenizeCallback: PSPayPalTokenizeCallback

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockPSApiClient = mockk(relaxed = true)
        mockActivity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        mockPSTokenizationService = mockk<PSTokenization>()
        mockPayPalNativeCheckoutClient = mockk<PayPalNativeCheckoutClient>()
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        every { mockPSApiClient.getCorrelationId() } returns correlationId
        mockPSPayPalTokenizeCallback = mockk<PSPayPalTokenizeCallback>()
        justRun { mockPSPayPalTokenizeCallback.onFailure(any()) }
        justRun { mockPSPayPalTokenizeCallback.onCancelled(any()) }
        justRun { mockPSPayPalTokenizeCallback.onSuccess(any()) }
    }

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    private fun providePSPayPalNativeController() = PSPayPalNativeController(
        lifecycleScope = mockActivity.lifecycleScope,
        psApiClient = mockPSApiClient,
        tokenizationService = mockPSTokenizationService,
        payPalNativeCheckoutClient = mockPayPalNativeCheckoutClient
    )

    @Test
    fun `IF dispose THEN data is cleared`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            every { mockPayPalNativeCheckoutClient.listener } answers { callOriginal() }
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.dispose()

            // Verify
            assertNull(mockPayPalNativeCheckoutClient.listener)
        }

    @Test
    fun `IF startPayPalCheckout THEN payPalNativeCheckoutClient startCheckout is called`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.startCheckout(any()) }
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.startPayPalCheckout(
                context = mockActivity,
                orderId = orderId
            )

            // Verify
            verify {
                mockPayPalNativeCheckoutClient.startCheckout(any())
                mockPayPalNativeCheckoutClient.listener = any()
            }
        }

    @Test
    fun `IF onPayPalCheckoutCanceled THEN onPayPalCanceled is called & data is cleared`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            every { mockPayPalNativeCheckoutClient.listener } answers { callOriginal() }
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.onPayPalCheckoutCanceled()

            // Verify
            verify(exactly = 1) {
                mockPSPayPalTokenizeCallback.onCancelled(payPalUserCancelledException(correlationId))
            }
            assertNull(mockPayPalNativeCheckoutClient.listener)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutFailure THEN onPayPalFailure is called & data is cleared`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            every { mockPayPalNativeCheckoutClient.listener } answers { callOriginal() }
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.onPayPalCheckoutFailure(
                PayPalSDKError(
                    code = 0,
                    errorDescription = ""
                )
            )

            // Verify
            verify(exactly = 1) {
                mockPSPayPalTokenizeCallback.onFailure(
                    payPalFailedAuthorizationException(correlationId)
                )
            }
            assertNull(mockPayPalNativeCheckoutClient.listener)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess THEN onPayPalSuccess is called & data is cleared`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            every { mockPayPalNativeCheckoutClient.listener } answers { callOriginal() }
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.onPayPalCheckoutSuccess(
                PayPalNativeCheckoutResult(
                    orderId = orderId,
                    payerId = ""
                )
            )

            // Verify
            assertNull(mockPayPalNativeCheckoutClient.listener)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutStart THEN log is logged`() =
        runTest {
            // Arrange
            mockkObject(LocalLog)
            justRun { LocalLog.d(any(), any()) }
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.onPayPalCheckoutStart()

            // Verify
            verify(exactly = 1) {
                LocalLog.d("PSPayPalNativeController", "onPayPalCheckoutStart")
            }
        }

}