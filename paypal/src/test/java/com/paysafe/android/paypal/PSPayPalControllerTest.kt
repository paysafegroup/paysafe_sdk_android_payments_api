/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.model.config.PSEnvironment
import com.paysafe.android.paymentmethods.PaymentMethodsServiceImpl
import com.paysafe.android.paymentmethods.domain.model.AccountConfiguration
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType
import com.paysafe.android.paypal.domain.model.PSPayPalConfig
import com.paysafe.android.paypal.domain.model.PSPayPalRenderType
import com.paysafe.android.paypal.domain.model.PSPayPalTokenizeOptions
import com.paysafe.android.paypal.exception.amountShouldBePositiveException
import com.paysafe.android.paypal.exception.currencyCodeInvalidIsoException
import com.paysafe.android.paypal.exception.genericApiErrorException
import com.paysafe.android.paypal.exception.improperlyCreatedMerchantAccountConfigException
import com.paysafe.android.paypal.exception.invalidAccountIdForPaymentMethodException
import com.paysafe.android.paypal.exception.payPalFailedAuthorizationException
import com.paysafe.android.paypal.exception.payPalUserCancelledException
import com.paysafe.android.paypal.exception.tokenizationAlreadyInProgressException
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PSPayPalLanguage
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PSPayPalShippingPreference
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PayPalRequest
import io.mockk.clearAllMocks
import io.mockk.coEvery
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PSPayPalControllerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val accountIdInput = "accountId"
    private val currencyCodeInput = "USD"
    private val applicationIdInput = "applicationId"
    private val clientIdInput = "clientIdInput"
    private val correlationId = "testCorrelationId"
    private val application = RuntimeEnvironment.getApplication()

    private val psPayPalConfigValidInput = PSPayPalConfig(
        currencyCode = currencyCodeInput,
        accountId = accountIdInput,
        renderType = PSPayPalRenderType.PSPayPalNativeRenderType(applicationIdInput)
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
    private lateinit var mockLifecycleScope: LifecycleCoroutineScope
    private lateinit var mockActivityResultCaller: ActivityResultCaller
    private lateinit var mockPSTokenizationService: PSTokenizationService
    private lateinit var mockPayPalNativeCheckoutClient: PayPalNativeCheckoutClient
    private lateinit var mockPSPayPalTokenizeCallback: PSPayPalTokenizeCallback

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockPSApiClient = mockk(relaxed = true)
        mockActivity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        mockLifecycleScope = mockk<LifecycleCoroutineScope>(relaxed = true)
        mockActivityResultCaller = mockk<ActivityResultCaller>(relaxed = true)
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
    fun `IF initialize with invalid currencyCode THEN initialize RETURNS Failure with currencyCodeInvalidIsoException`() =
        runTest {
            // Arrange

            // Act
            val result = PSPayPalController.initialize(
                activityResultCaller = mockActivityResultCaller,
                application = application,
                lifecycleScope = mockLifecycleScope,
                config = psPayPalConfigValidInput.copy(
                    currencyCode = "wrongCurrencyCode"
                ),
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(currencyCodeInvalidIsoException(correlationId), exception)
        }

    @Test
    fun `IF initialize with web renderType and validatePaymentMethods returns Failure THEN initialize RETURNS Failure`() =
        runTest {
            // Arrange
            mockkObject(PSPayPalController)
            val toReturnResult = PSResult.Failure(Exception())
            coEvery {
                PSPayPalController.validatePaymentMethods(any(), any(), any(), any())
            } returns toReturnResult

            // Act
            val result = PSPayPalController.initialize(
                activityResultCaller = mockActivityResultCaller,
                application = application,
                lifecycleScope = mockLifecycleScope,
                config = PSPayPalConfig(
                    currencyCode = currencyCodeInput,
                    accountId = accountIdInput,
                    renderType = PSPayPalRenderType.PSPayPalWebRenderType
                ),
                psApiClient = mockPSApiClient
            )

            // Assert
            assertEquals(toReturnResult, result)
        }

    @Test
    fun `IF initialize and validatePaymentMethods returns Success with null THEN initialize RETURNS Failure with improperlyCreatedMerchantAccountConfigException`() =
        runTest {
            // Arrange
            mockkObject(PSPayPalController)
            coEvery {
                PSPayPalController.validatePaymentMethods(any(), any(), any(), any())
            } returns PSResult.Success(null)

            // Act
            val result = PSPayPalController.initialize(
                activityResultCaller = mockActivityResultCaller,
                application = application,
                lifecycleScope = mockLifecycleScope,
                config = psPayPalConfigValidInput,
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(improperlyCreatedMerchantAccountConfigException(correlationId), exception)
        }

    @Test
    fun `IF initialize and validatePaymentMethods returns Success THEN initialize RETURNS Success`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            mockkObject(PSPayPalController)
            val validatePaymentMethodsResult = PSResult.Success(clientIdInput)
            coEvery {
                PSPayPalController.validatePaymentMethods(any(), any(), any(), any())
            } returns validatePaymentMethodsResult
            val mockPSPayPalController = mockk<PSPayPalController>()
            coEvery {
                PSPayPalController.handleValidatePaymentMethodsResultSuccess(
                    validatePaymentMethodsResult = validatePaymentMethodsResult,
                    psApiClient = any(),
                    config = any(),
                    application = any(),
                    lifecycleScope = any(),
                    psPayPalWebController = any()
                )
            } returns PSResult.Success(mockPSPayPalController)

            // Act
            val result = PSPayPalController.initialize(
                activityResultCaller = mockActivityResultCaller,
                application = application,
                lifecycleScope = mockLifecycleScope,
                config = psPayPalConfigValidInput,
                psApiClient = mockPSApiClient
            )
            val controller = (result as PSResult.Success).value!!

            // Assert
            assertEquals(mockPSPayPalController, controller)
        }

    @Test
    fun `IF validatePaymentMethods returns Success with PROD environment THEN validatePaymentMethods RETURNS Success`() =
        runTest {
            // Arrange
            every { mockPSApiClient.environment } returns PSEnvironment.PROD
            mockkObject(PSPayPalController)
            val validatePaymentMethodsResult = PSResult.Success(clientIdInput)

            // Act
            val result = PSPayPalController.handleValidatePaymentMethodsResultSuccess(
                validatePaymentMethodsResult = validatePaymentMethodsResult,
                psApiClient = mockPSApiClient,
                config = psPayPalConfigValidInput,
                application = application,
                lifecycleScope = mockLifecycleScope,
                psPayPalWebController = null
            )
            val controller = (result as PSResult.Success).value!!

            // Assert
            assertNotNull(controller)
        }

    @Test
    fun `IF encodeToString THEN logEventContent matches expectedLogString`() =
        runTest {
            // Arrange
            val logEventContent = with(psPayPalConfigValidInput) {
                LogEventContent(
                    currencyCode = currencyCode,
                    accountId = accountId
                )
            }
            val expectedLogString =
                "{\"currencyCode\":\"${logEventContent.currencyCode}\",\"accountId\":\"${logEventContent.accountId}\"}"

            // Act
            val encodedLogString = Json.encodeToString(logEventContent)

            // Assert
            assertEquals(expectedLogString, encodedLogString)
        }

    @Test
    fun `IF handleValidatePaymentMethodsResultSuccess with web renderType returns Success THEN handleValidatePaymentMethodsResultSuccess RETURNS Success`() =
        runTest {
            // Arrange
            val mockPSPayPalWebController = mockk<PSPayPalWebController>(relaxed = true)

            // Act
            val result = PSPayPalController.handleValidatePaymentMethodsResultSuccess(
                validatePaymentMethodsResult = PSResult.Success(clientIdInput),
                psApiClient = mockPSApiClient,
                config = PSPayPalConfig(
                    currencyCode = currencyCodeInput,
                    accountId = accountIdInput,
                    renderType = PSPayPalRenderType.PSPayPalWebRenderType
                ),
                application = application,
                lifecycleScope = mockLifecycleScope,
                psPayPalWebController = mockPSPayPalWebController
            )
            val controller = (result as PSResult.Success).value!!

            // Assert
            assertNotNull(controller)
        }

    @Test
    fun `IF validatePaymentMethods and getPaymentMethods returns Failure THEN validatePaymentMethods RETURNS Failure`() =
        runTest {
            // Arrange
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val expectedException = Exception()
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(currencyCodeInput)
            } returns PSResult.Failure(expectedException)

            // Act
            val result = PSPayPalController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(expectedException, exception)
        }

    @Test
    fun `IF validatePaymentMethods and getPaymentMethods returns Success THEN validatePaymentMethods RETURNS Success`() =
        runTest {
            // Arrange
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val paymentMethodsValid = listOf(
                PaymentMethod(
                    paymentMethod = PaymentMethodType.PAYPAL,
                    accountId = accountIdInput,
                    currencyCode = currencyCodeInput,
                    accountConfiguration = AccountConfiguration(clientId = clientIdInput)
                )
            )
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(currencyCodeInput)
            } returns PSResult.Success(paymentMethodsValid)

            // Act
            val result = PSPayPalController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )
            val clientId = (result as PSResult.Success).value

            // Assert
            assertEquals(clientIdInput, clientId)
        }


    @Test
    fun `IF validatePaymentMethods and getPaymentMethods returns no PayPal type THEN validatePaymentMethods RETURNS Failure with invalidAccountIdForPaymentMethodException`() =
        runTest {
            // Arrange
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val paymentMethodsValid = listOf(
                PaymentMethod(
                    paymentMethod = PaymentMethodType.CARD,
                    accountId = accountIdInput,
                    currencyCode = currencyCodeInput,
                    accountConfiguration = AccountConfiguration(clientId = clientIdInput)
                )
            )
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(currencyCodeInput)
            } returns PSResult.Success(paymentMethodsValid)

            // Act
            val result = PSPayPalController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(invalidAccountIdForPaymentMethodException(correlationId), exception)
        }


    @Test
    fun `IF validatePaymentMethods and getPaymentMethods returns invalid accountId type THEN validatePaymentMethods RETURNS Failure with improperlyCreatedMerchantAccountConfigException`() =
        runTest {
            // Arrange
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val paymentMethodsValid = listOf(
                PaymentMethod(
                    paymentMethod = PaymentMethodType.PAYPAL,
                    accountId = "invalidAccountId",
                    currencyCode = currencyCodeInput,
                    accountConfiguration = AccountConfiguration(clientId = clientIdInput)
                )
            )
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(currencyCodeInput)
            } returns PSResult.Success(paymentMethodsValid)

            // Act
            val result = PSPayPalController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(improperlyCreatedMerchantAccountConfigException(correlationId), exception)
        }

    @Test
    fun `IF tokenize and tokenizationAlreadyInProgress THEN tokenize RETURNS via callback on Failure with tokenizationAlreadyInProgressException`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.tokenizationAlreadyInProgress = true
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions.copy(amount = 0),
                callback = mockPSPayPalTokenizeCallback
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(
                    tokenizationAlreadyInProgressException(correlationId)
                )
            }
        }

    @Test
    fun `IF tokenize and amount not valid THEN tokenize RETURNS via callback on Failure with amountShouldBePositiveException`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions.copy(amount = 0),
                callback = mockPSPayPalTokenizeCallback
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(amountShouldBePositiveException(correlationId))
            }
        }

    @Test
    fun `IF tokenize and lifecycleScope is null THEN tokenize RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            coEvery { mockPSTokenizationService.tokenize(any()) } returns PSResult.Success()
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.lifecycleScopeWeakRef.clear()
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions,
                callback = mockPSPayPalTokenizeCallback,
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
        }

    @Test
    fun `IF tokenize and PSTokenization tokenize returns Failure THEN tokenize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            val exceptedException = Exception()
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Failure(exceptedException)
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions,
                callback = mockPSPayPalTokenizeCallback,
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(exceptedException)
            }
        }

    @Test
    fun `IF tokenize and PSTokenization tokenize returns Success with null THEN tokenize RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success(null)
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions,
                callback = mockPSPayPalTokenizeCallback,
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
        }

    @Test
    fun `IF tokenize and PSTokenization tokenize returns Success with PaymentHandle payPalOrderId null THEN tokenize RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            val expectedPaymentHandle = PaymentHandle(
                merchantRefNum = "",
                paymentHandleToken = "",
                status = "",
                payPalOrderId = null
            )
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success(expectedPaymentHandle)
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions,
                callback = mockPSPayPalTokenizeCallback,
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
        }

    @Test
    fun `IF tokenize and PSTokenization tokenize returns Success THEN tokenize starts PayPal checkout`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            val expectedPaymentHandle = PaymentHandle(
                merchantRefNum = "",
                paymentHandleToken = "",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success(expectedPaymentHandle)
            val psPayPalController = providePSPayPalNativeController()
            justRun { mockPayPalNativeCheckoutClient.startCheckout(any()) }
            justRun { mockPayPalNativeCheckoutClient.listener = any() }

            // Act
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions,
                callback = mockPSPayPalTokenizeCallback,
            )

            // Verify
            verify {
                mockPayPalNativeCheckoutClient.listener = any()
                mockPayPalNativeCheckoutClient.startCheckout(any())
            }
        }

    @Test
    fun `IF handleTokenizeResultSuccess with tokenizeCallback null and PaymentHandle null THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.handleTokenizeResultSuccess(
                context = mockActivity,
                result = PSResult.Success(null)
            )

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with tokenizeCallback null and payPalOrderId null THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.handleTokenizeResultSuccess(
                context = mockActivity,
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "",
                        paymentHandleToken = "",
                        status = "",
                        payPalOrderId = null
                    )
                )
            )

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultFailure with tokenizeCallback null THEN handleTokenizeResultFailure RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psPayPalController = providePSPayPalNativeController()

            // Act
            psPayPalController.handleTokenizeResultFailure(
                result = PSResult.Failure(Exception())
            )

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and paymentHandle null THEN onPayPalCheckoutSuccess RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success()
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions,
                callback = mockPSPayPalTokenizeCallback,
            )
            psPayPalController.paymentHandle = null

            // Act
            psPayPalController.onPayPalSuccess()

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and paymentHandle & tokenizeCallback are nulls THEN onPayPalCheckoutSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.paymentHandle = null

            // Act
            psPayPalController.onPayPalSuccess()

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and lifecycleScope is null THEN onPayPalCheckoutSuccess RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            val expectedPaymentHandle = PaymentHandle(
                merchantRefNum = "",
                paymentHandleToken = "",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success(expectedPaymentHandle)
            val mockListener = mockk<PayPalNativeCheckoutListener>()
            every { mockPayPalNativeCheckoutClient.listener } returns mockListener
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            justRun { mockPayPalNativeCheckoutClient.startCheckout(any()) }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.tokenize(
                context = mockActivity,
                payPalTokenizeOptions = psPayPalTokenizeOptions,
                callback = mockPSPayPalTokenizeCallback,
            )
            psPayPalController.lifecycleScopeWeakRef.clear()

            // Act
            psPayPalController.onPayPalSuccess()

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and lifecycleScope & tokenizeCallback are nulls THEN onPayPalCheckoutSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            psPayPalController.paymentHandle = paymentHandle
            psPayPalController.lifecycleScopeWeakRef.clear()

            // Act
            psPayPalController.onPayPalSuccess()

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and PSTokenization refreshToken returns Success with null THEN onPayPalCheckoutSuccess RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Success()
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalSuccess(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and PSTokenization refreshToken returns Success with null and tokenizeCallback is null THEN onPayPalCheckoutSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Success()
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalSuccess(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and PSTokenization refreshToken returns Success THEN onPayPalCheckoutSuccess RETURNS via callback onSuccess`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Success(paymentHandle)
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalSuccess(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onSuccess(any())
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and PSTokenization refreshToken returns Success and tokenizeCallback is null THEN onPayPalCheckoutSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Success(paymentHandle)
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalSuccess(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and PSTokenization refreshToken returns Failure THEN onPayPalCheckoutSuccess RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Failure(Exception())
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalSuccess(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutSuccess and PSTokenization refreshToken returns Failure and tokenizeCallback is null THEN onPayPalCheckoutSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Failure(Exception())
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalSuccess(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutFailure THEN callback RETURNS onFailure with payPalFailedAuthorizationException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Failure(Exception())
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalFailure()

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onFailure(
                    payPalFailedAuthorizationException(correlationId)
                )
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutFailure and tokenizeCallback is null THEN callback RETURNS nothing`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Failure(Exception())
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalFailure()

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutCanceled THEN callback RETURNS onCancelled with payPalUserCancelledException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Failure(Exception())
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.tokenizeCallback = mockPSPayPalTokenizeCallback
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalCanceled()

            // Verify
            verify {
                mockPSPayPalTokenizeCallback.onCancelled(payPalUserCancelledException(correlationId))
            }
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onPayPalCheckoutCanceled and tokenizeCallback is null THEN callback RETURNS nothing`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                payPalOrderId = "payPalOrderId"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Failure(Exception())
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.paymentHandle = paymentHandle

            // Act
            psPayPalController.onPayPalCanceled()

            // Assert
            assertNull(psPayPalController.tokenizeCallback)
            assertFalse(psPayPalController.tokenizationAlreadyInProgress)
        }

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
            assertNull(psPayPalController.tokenizeCallback)
            assertNull(psPayPalController.paymentHandle)
            assertNull(psPayPalController.lifecycleScopeWeakRef.get())
        }

    @Test
    fun `IF dispose and lifecycle is null THEN data is cleared`() =
        runTest {
            // Arrange
            justRun { mockPayPalNativeCheckoutClient.listener = any() }
            every { mockPayPalNativeCheckoutClient.listener } answers { callOriginal() }
            val psPayPalController = providePSPayPalNativeController()
            psPayPalController.lifecycleScopeWeakRef.clear()

            // Act
            psPayPalController.dispose()

            // Verify
            assertNull(mockPayPalNativeCheckoutClient.listener)
            assertNull(psPayPalController.tokenizeCallback)
            assertNull(psPayPalController.paymentHandle)
            assertNull(psPayPalController.lifecycleScopeWeakRef.get())
        }

}