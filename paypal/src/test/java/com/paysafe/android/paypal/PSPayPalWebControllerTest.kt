/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.lifecycleScope
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.paypal.activity.PayPalConstants
import com.paysafe.android.paypal.exception.genericApiErrorException
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSPayPalWebControllerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val correlationId = "testCorrelationId"
    private val orderId = "orderId"
    private val clientId = "clientId"

    private lateinit var mockActivity: AppCompatActivity
    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockActivityResultCaller: ActivityResultCaller
    private lateinit var mockPSTokenizationService: PSTokenizationService
    private lateinit var mockPSPayPalTokenizeCallback: PSPayPalTokenizeCallback

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockPSApiClient = mockk(relaxed = true)
        mockActivity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        mockActivityResultCaller = mockk<ActivityResultCaller>(relaxed = true)
        mockPSTokenizationService = mockk<PSTokenization>()
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        every { mockPSApiClient.getCorrelationId() } returns correlationId
        justRun { mockPSApiClient.logErrorEvent(any(), any()) }
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

    private fun providePSPayPalWebController() = PSPayPalWebController(
        activityResultCaller = mockActivityResultCaller,
        lifecycleScope = mockActivity.lifecycleScope,
        psApiClient = mockPSApiClient,
        tokenizationService = mockPSTokenizationService
    ).apply {
        setupController(clientId)
    }

    @Test
    fun `IF dispose THEN data is cleared`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalWebController()

            // Act
            psPayPalController.dispose()

            // Verify
            verify(exactly = 1) {
                psPayPalController.activityResultLauncher.unregister()
            }
        }

    @Test
    fun `IF startPayPalCheckout THEN intent is launched via activityResultLauncher`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalWebController()

            // Act
            psPayPalController.startPayPalCheckout(
                context = mockActivity,
                orderId = orderId
            )

            // Verify
            verify(exactly = 1) {
                psPayPalController.activityResultLauncher.launch(any())
            }
        }

    @Test
    fun `IF handleActivityResult with RESULT_SUCCESS THEN onPayPalSuccess is called`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalWebController()

            // Act
            psPayPalController.handleActivityResult(PayPalConstants.RESULT_SUCCESS)

            // Verify
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleActivityResult with RESULT_CANCELED THEN onPayPalCanceled is called`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalWebController()

            // Act
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.handleActivityResult(PayPalConstants.RESULT_CANCELED)

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onCancelled(payPalUserCancelledException(correlationId))
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleActivityResult with RESULT_FAILED THEN onPayPalFailure is called`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalWebController()

            // Act
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.handleActivityResult(PayPalConstants.RESULT_FAILED)

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(
                    payPalFailedAuthorizationException(correlationId)
                )
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleActivityResult with unknown code THEN onPayPalFailure is called`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalWebController()
            val unknownResultCode = -1

            // Act
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.handleActivityResult(unknownResultCode)

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }


}