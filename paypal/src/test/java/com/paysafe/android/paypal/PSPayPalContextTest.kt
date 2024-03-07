/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.paypal.domain.model.PSPayPalConfig
import com.paysafe.android.paypal.domain.model.PSPayPalRenderType
import com.paysafe.android.paypal.domain.model.PSPayPalTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PSPayPalLanguage
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PSPayPalShippingPreference
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PayPalRequest
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PSPayPalContextTest {

    private val accountIdInput = "accountId"
    private val currencyCodeInput = "USD"
    private val psPayPalConfigValidInput = PSPayPalConfig(
        currencyCode = currencyCodeInput,
        accountId = accountIdInput,
        renderType = PSPayPalRenderType.PSPayPalNativeRenderType("applicationId")
    )
    private val psPayPalTokenizeOptions = PSPayPalTokenizeOptions(
        amount = 100,
        currencyCode = currencyCodeInput,
        transactionType = TransactionType.PAYMENT,
        merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
        accountId = accountIdInput,
        payPalRequest = PayPalRequest(
            consumerId = "consumerId",
            recipientDescription = "recipientDescription",
            language = PSPayPalLanguage.US,
            shippingPreference = PSPayPalShippingPreference.SET_PROVIDED_ADDRESS,
            consumerMessage = "consumerMessage",
            orderDescription = "orderDescription"
        )
    )

    private lateinit var mockActivity: AppCompatActivity
    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockPSCallback: PSCallback<PSPayPalContext>

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockActivity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        mockPSApiClient = mockk(relaxed = true)
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        mockPSCallback = mockk<PSCallback<PSPayPalContext>>()
        justRun { mockPSCallback.onFailure(any()) }
        justRun { mockPSCallback.onSuccess(any()) }
    }

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `IF initialize with fragment and PaysafeSDK not initialized THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            val fragment = Fragment()
            mockActivity.supportFragmentManager.beginTransaction().apply {
                add(fragment, "TestFragment")
                commitNow()
            }

            // Act
            PSPayPalContext.initialize(
                fragment,
                psPayPalConfigValidInput,
                mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }

    @Test
    fun `IF initialize with activity and PaysafeSDK not initialized THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange

            // Act
            PSPayPalContext.initialize(
                mockActivity,
                psPayPalConfigValidInput,
                mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }


    @Test
    fun `IF initialize and Controller returns Failure THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            every { PaysafeSDK.isInitialized() } returns true
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            mockkObject(PSPayPalController)
            coEvery {
                PSPayPalController.initialize(any(), any(), any(), any(), any())
            } returns PSResult.Failure(Exception())

            // Act
            PSPayPalContext.initialize(
                mockActivity,
                psPayPalConfigValidInput,
                mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }


    @Test
    fun `IF initialize and Controller returns Success with null THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            every { PaysafeSDK.isInitialized() } returns true
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            mockkObject(PSPayPalController)
            coEvery {
                PSPayPalController.initialize(any(), any(), any(), any(), any())
            } returns PSResult.Success(null)

            // Act
            PSPayPalContext.initialize(
                mockActivity,
                psPayPalConfigValidInput,
                mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }


    @Test
    fun `IF initialize and Controller throws exception THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            every { PaysafeSDK.isInitialized() } returns true
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            mockkObject(PSPayPalController)
            coEvery {
                PSPayPalController.initialize(any(), any(), any(), any(), any())
            } throws Exception()

            // Act
            PSPayPalContext.initialize(
                mockActivity,
                psPayPalConfigValidInput,
                mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }


    @Test
    fun `IF initialize and Controller returns Success THEN initialize RETURNS via callback onSuccess`() =
        runTest {
            // Arrange
            every { PaysafeSDK.isInitialized() } returns true
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            mockkObject(PSPayPalController)
            val mockPSPayPalController = mockk<PSPayPalController>()
            coEvery {
                PSPayPalController.initialize(any(), any(), any(), any(), any())
            } returns PSResult.Success(mockPSPayPalController)

            // Act
            PSPayPalContext.initialize(
                mockActivity,
                psPayPalConfigValidInput,
                mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onSuccess(any())
            }
        }


    @Test
    fun `IF tokenize THEN controller tokenize is called`() =
        runTest {
            // Arrange
            val mockPSCallback = mockk<PSPayPalTokenizeCallback>()
            mockkObject(PSPayPalController)
            val mockPSPayPalController = mockk<PSPayPalController>()
            coJustRun { mockPSPayPalController.tokenize(any(), any(), mockPSCallback) }
            val psPayPalContext = PSPayPalContext(mockPSPayPalController)

            // Act
            psPayPalContext.tokenize(
                mockActivity,
                psPayPalTokenizeOptions,
                mockPSCallback
            )

            // Verify
            coVerify(exactly = 1) {
                mockPSPayPalController.tokenize(
                    mockActivity,
                    psPayPalTokenizeOptions,
                    mockPSCallback
                )
            }
        }


    @Test
    fun `IF dispose THEN controller dispose is called`() =
        runTest {
            // Arrange
            val mockPSPayPalController = mockk<PSPayPalController>()
            justRun { mockPSPayPalController.dispose() }
            val psPayPalContext = PSPayPalContext(mockPSPayPalController)

            // Act
            psPayPalContext.dispose()

            // Verify
            verify(exactly = 1) {
                mockPSPayPalController.dispose()
            }
        }

}