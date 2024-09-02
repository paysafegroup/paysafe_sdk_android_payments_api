package com.paysafe.android.venmo

import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.VenmoClient
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.brainTreeDetails.data.BrainTreeDetailsService
import com.paysafe.android.brainTreeDetails.data.entity.BrainTreeDetailsResponse
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.paymentmethods.PaymentMethodsServiceImpl
import com.paysafe.android.paymentmethods.domain.model.AccountConfiguration
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.data.entity.paymentHandle.AuthenticationPurposeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.GatewayResponseSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.MerchantDescriptorSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.MessageCategorySerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleResponseSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleStatusRequest
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentHandleStatusResponse
import com.paysafe.android.tokenization.data.entity.paymentHandle.PaymentTypeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ReturnLinkRelationSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ReturnLinkSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.ThreeDSSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.TransactionIntentSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.TransactionTypeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.DateOfBirthSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.GenderSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.IdentityDocumentSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.ProfileLocaleSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.profile.ProfileSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.BillingDetailsRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.CardExpiryRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.CardRequestSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.ShippingDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.ShippingMethodSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.response.CardResponseSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.response.NetworkTokenResponseSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.venmo.VenmoRequestSerializable
import com.paysafe.android.tokenization.data.mapper.paymentHandleTokenStatusToDomain
import com.paysafe.android.tokenization.data.mapper.toData
import com.paysafe.android.tokenization.data.mapper.toDomain
import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.ReturnLinkRelation
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.DateOfBirth
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Gender
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.IdentityDocument
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.ProfileLocale
import com.paysafe.android.tokenization.domain.model.paymentHandle.venmo.VenmoRequest
import com.paysafe.android.venmo.domain.model.PSVenmoConfig
import com.paysafe.android.venmo.domain.model.PSVenmoTokenizeOptions
import com.paysafe.android.venmo.exception.amountShouldBePositiveException
import com.paysafe.android.venmo.exception.currencyCodeInvalidIsoException
import com.paysafe.android.venmo.exception.genericApiErrorException
import com.paysafe.android.venmo.exception.improperlyCreatedMerchantAccountConfigException
import com.paysafe.android.venmo.exception.invalidAccountIdForPaymentMethodException
import com.paysafe.android.venmo.exception.tokenizationAlreadyInProgressException
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
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PSVenmoControllerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val accountIdInput = "accountId"
    private val currencyCodeInput = "USD"
    private val clientIdInput = "clientIdInput"
    private val correlationId = "testCorrelationId"

    private val psVenmoTokenizeOptions = PSVenmoTokenizeOptions(
        amount = 100,
        currencyCode = currencyCodeInput,
        transactionType = TransactionType.PAYMENT,
        merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
        accountId = accountIdInput,
        venmoRequest = VenmoRequest(
            consumerId = "consumerId",
            merchantAccountId = "merchantAccountId",
            profileId = "profileId"
        ),
        customUrlScheme = "customUrlScheme"
    )

    private lateinit var mockActivity: AppCompatActivity
    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockLifecycleScope: LifecycleCoroutineScope
    private lateinit var mockActivityResultCaller: ActivityResultCaller
    private lateinit var mockPSTokenizationService: PSTokenizationService
    private lateinit var mockVenmoClient: VenmoClient
    private lateinit var mockBraintreeClient: BraintreeClient
    private lateinit var mockPSVenmoTokenizeCallback: PSVenmoTokenizeCallback
    private lateinit var mockBrainTreeDetailsService: BrainTreeDetailsService

    private val psVenmoConfig = PSVenmoConfig(
        currencyCode = currencyCodeInput,
        accountId = accountIdInput,
    )


    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockPSApiClient = mockk(relaxed = true)
        mockActivity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        mockLifecycleScope = mockk<LifecycleCoroutineScope>(relaxed = true)
        mockActivityResultCaller = mockk<ActivityResultCaller>(relaxed = true)
        mockVenmoClient = mockk(relaxed = true)
        mockBraintreeClient = mockk(relaxed = true)
        mockBrainTreeDetailsService = mockk<BrainTreeDetailsService>()

        mockPSTokenizationService = mockk<PSTokenization>()
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        every { mockPSApiClient.getCorrelationId() } returns correlationId
        mockPSVenmoTokenizeCallback = mockk<PSVenmoTokenizeCallback>()
        justRun { mockPSVenmoTokenizeCallback.onFailure(any()) }
        justRun { mockPSVenmoTokenizeCallback.onCancelled(any()) }
        justRun { mockPSVenmoTokenizeCallback.onSuccess(any()) }
    }

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    private fun providePSVenmoNativeController() = PSVenmoNativeController(
        activityResultCaller = mockActivityResultCaller,
        lifecycleScope = mockActivity.lifecycleScope,
        psApiClient = mockPSApiClient,
        tokenizationService = mockPSTokenizationService,
    )


    @Test
    fun `IF initialize with invalid currencyCode THEN initialize RETURNS Failure with currencyCodeInvalidIsoException`() =
        runTest {
            // Arrange

            // Act
            val result = PSVenmoController.initialize(
                activityResultCaller = mockActivityResultCaller,
                lifecycleScope = mockLifecycleScope,
                config = psVenmoConfig.copy(
                    currencyCode = "wrongCurrencyCode"
                ),
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            Assert.assertEquals(currencyCodeInvalidIsoException(correlationId), exception)
        }

    @Test
    fun `IF initialize  validatePaymentMethods returns Failure THEN initialize RETURNS Failure`() =
        runTest {
            // Arrange
            mockkObject(PSVenmoController)
            val toReturnResult = PSResult.Failure(Exception())
            coEvery {
                PSVenmoController.validatePaymentMethods(any(), any(), any(), any())
            } returns toReturnResult

            // Act
            val result = PSVenmoController.initialize(
                activityResultCaller = mockActivityResultCaller,
                lifecycleScope = mockLifecycleScope,
                config = PSVenmoConfig(
                    currencyCode = currencyCodeInput,
                    accountId = accountIdInput,
                ),
                psApiClient = mockPSApiClient
            )

            // Assert
            Assert.assertEquals(toReturnResult, result)
        }

    @Test
    fun `IF initialize and validatePaymentMethods returns Success THEN initialize RETURNS Success`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            mockkObject(PSVenmoController)
            val validatePaymentMethodsResult = PSResult.Success(clientIdInput)
            coEvery {
                PSVenmoController.validatePaymentMethods(any(), any(), any(), any())
            } returns validatePaymentMethodsResult
            val mockPSVenmoController = mockk<PSVenmoController>()
            coEvery {
                PSVenmoController.handleValidatePaymentMethodsResultSuccess(
                    psVenmoNativeController = any()
                )
            } returns PSResult.Success(mockPSVenmoController)

            // Act
            val result = PSVenmoController.initialize(
                activityResultCaller = mockActivityResultCaller,
                lifecycleScope = mockLifecycleScope,
                config = psVenmoConfig,
                psApiClient = mockPSApiClient
            )
            val controller = (result as PSResult.Success).value!!

            // Assert
            Assert.assertEquals(mockPSVenmoController, controller)
        }

    @Test
    fun `IF handleValidatePaymentMethodsResultSuccess with VenmoNativeController returns Success THEN handleValidatePaymentMethodsResultSuccess RETURNS Success`() =
        runTest {
            // Arrange
            val mockPSVenmoNativeController = mockk<PSVenmoNativeController>(relaxed = true)

            // Act
            val result = PSVenmoController.handleValidatePaymentMethodsResultSuccess(
                psVenmoNativeController = mockPSVenmoNativeController
            )
            val controller = (result as PSResult.Success).value!!

            // Assert
            Assert.assertNotNull(controller)
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
            val result = PSVenmoController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            Assert.assertEquals(expectedException, exception)
        }

    @Test
    fun `IF validatePaymentMethods and getPaymentMethods returns Success THEN validatePaymentMethods RETURNS Success`() =
        runTest {
            // Arrange
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val paymentMethodsValid = listOf(
                PaymentMethod(
                    paymentMethod = PaymentMethodType.VENMO,
                    accountId = accountIdInput,
                    currencyCode = currencyCodeInput,
                    accountConfiguration = AccountConfiguration(clientId = clientIdInput)
                )
            )
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(currencyCodeInput)
            } returns PSResult.Success(paymentMethodsValid)

            // Act
            val result = PSVenmoController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )

            // Assert
            Assert.assertNotNull(result)
        }

    @Test
    fun `IF validatePaymentMethods and getPaymentMethods returns no Venmo type THEN validatePaymentMethods RETURNS Failure with invalidAccountIdForPaymentMethodException`() =
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
            val result = PSVenmoController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            Assert.assertEquals(invalidAccountIdForPaymentMethodException(correlationId), exception)
        }

    @Test
    fun `IF validatePaymentMethods and getPaymentMethods returns invalid accountId type THEN validatePaymentMethods RETURNS Failure with improperlyCreatedMerchantAccountConfigException`() =
        runTest {
            // Arrange
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val paymentMethodsValid = listOf(
                PaymentMethod(
                    paymentMethod = PaymentMethodType.VENMO,
                    accountId = "invalidAccountId",
                    currencyCode = currencyCodeInput,
                    accountConfiguration = AccountConfiguration(clientId = clientIdInput)
                )
            )
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(currencyCodeInput)
            } returns PSResult.Success(paymentMethodsValid)

            // Act
            val result = PSVenmoController.validatePaymentMethods(
                currencyCode = currencyCodeInput,
                accountId = accountIdInput,
                paymentMethodService = mockPaymentMethodsService,
                psApiClient = mockPSApiClient
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            Assert.assertEquals(
                improperlyCreatedMerchantAccountConfigException(correlationId),
                exception
            )
        }

    @Test
    fun `IF tokenize and tokenizationAlreadyInProgress THEN tokenize RETURNS via callback on Failure with tokenizationAlreadyInProgressException`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.tokenizationAlreadyInProgress = true
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions.copy(amount = 0),
                callback = mockPSVenmoTokenizeCallback
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(
                    tokenizationAlreadyInProgressException(correlationId)
                )
            }
        }


    @Test
    fun `IF tokenize and amount not valid THEN tokenize RETURNS via callback on Failure with amountShouldBePositiveException`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions.copy(amount = 0),
                callback = mockPSVenmoTokenizeCallback
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(amountShouldBePositiveException(correlationId))
            }
        }

    @Test
    fun `IF tokenize and lifecycleScope is null THEN tokenize RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            coEvery { mockPSTokenizationService.tokenize(any()) } returns PSResult.Success()
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.lifecycleScopeWeakRef.clear()
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions,
                callback = mockPSVenmoTokenizeCallback,
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
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
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions,
                callback = mockPSVenmoTokenizeCallback,
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(exceptedException)
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
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions,
                callback = mockPSVenmoTokenizeCallback,
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
        }

    @Test
    fun `IF tokenize and PSTokenization tokenize returns Success with PaymentHandle Id null THEN tokenize RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            val expectedPaymentHandle = PaymentHandle(
                merchantRefNum = "",
                paymentHandleToken = "",
                status = "",
            )
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success(expectedPaymentHandle)
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions,
                callback = mockPSVenmoTokenizeCallback,
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
        }

    @Test
    fun `IF handleTokenizeResultSuccess with tokenizeCallback null and PaymentHandle null THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = "profile-id",
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(null)
            )

            // Assert
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with tokenizeCallback null and id null THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = "profile-id",
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "",
                        paymentHandleToken = "",
                        status = "",
                        id = null
                    )
                )
            )

            // Assert
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with status PAYABLE  THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = "profile-id",
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "merchantRefNum",
                        paymentHandleToken = "paymentHandleToken",
                        status = "PAYABLE",
                        id = "ID"
                    )
                )
            )

            // Assert
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with status PROCESSING  THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = "profile-id",
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "merchantRefNum",
                        paymentHandleToken = "paymentHandleToken",
                        status = "PROCESSING",
                        id = "ID"
                    )
                )
            )

            // Assert
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with status PROCESSING and with sesisonToken empty string THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = null,
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "merchantRefNum",
                        paymentHandleToken = "paymentHandleToken",
                        status = "PROCESSING",
                        id = "ID",
                        gatewayResponse = GatewayResponseSerializable(sessionToken = "")
                    )
                )
            )

            // Assert
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with status PROCESSING and with valid sesisonToken THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = "profile-id",
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "merchantRefNum",
                        paymentHandleToken = "paymentHandleToken",
                        status = "PROCESSING",
                        id = "ID",
                        gatewayResponse = GatewayResponseSerializable(
                            sessionToken = "session-token"
                        )
                    )
                )
            )

            // Assert
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with status PROCESSING and with empty gatewayResponse THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = null,
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "merchantRefNum",
                        paymentHandleToken = "paymentHandleToken",
                        status = "PROCESSING",
                        id = "ID",
                        gatewayResponse = GatewayResponseSerializable()
                    )
                )
            )

            // Assert
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultSuccess with status EXPIRED  THEN handleTokenizeResultSuccess RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultSuccess(
                context = mockActivity,
                profileId = "profile-id",
                amount = 1000,
                customUrlScheme = "",
                result = PSResult.Success(
                    PaymentHandle(
                        merchantRefNum = "merchantRefNum",
                        paymentHandleToken = "paymentHandleToken",
                        status = "EXPIRED",
                        id = "ID"
                    )
                )
            )

            // Assert
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleTokenizeResultFailure with tokenizeCallback null THEN handleTokenizeResultFailure RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.handleTokenizeResultFailure(
                result = PSResult.Failure(Exception())
            )

            // Assert
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onRefreshToken and paymentHandle null THEN onRefreshToken RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success()
            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions,
                callback = mockPSVenmoTokenizeCallback,
            )
            psVenmoController.paymentHandle = null

            // Act
            psVenmoController.onRefreshToken()

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
        }

    @Test
    fun `IF onRefreshToken and paymentHandle & tokenizeCallback are nulls THEN onRefreshToken RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.paymentHandle = null

            // Act
            psVenmoController.onRefreshToken()

            // Assert
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onRefreshToken and lifecycleScope is null THEN onRefreshToken RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            val expectedPaymentHandle = PaymentHandle(
                merchantRefNum = "",
                paymentHandleToken = "",
                status = "",
                id = "ID"
            )
            coEvery {
                mockPSTokenizationService.tokenize(any())
            } returns PSResult.Success(expectedPaymentHandle)

            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.tokenize(
                context = mockActivity,
                venmoTokenizeOptions = psVenmoTokenizeOptions,
                callback = mockPSVenmoTokenizeCallback,
            )
            psVenmoController.lifecycleScopeWeakRef.clear()

            // Act
            psVenmoController.onRefreshToken()

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onRefreshToken and lifecycleScope & tokenizeCallback are nulls THEN onRefreshToken RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                id = "ID"
            )
            psVenmoController.paymentHandle = paymentHandle
            psVenmoController.lifecycleScopeWeakRef.clear()

            // Act
            psVenmoController.onRefreshToken()

            // Assert
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onRefreshToken and PSTokenization refreshToken returns Success with null THEN onRefreshToken RETURNS via callback onFailure with genericApiErrorException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                id = "ID"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Success()
            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback
            psVenmoController.paymentHandle = paymentHandle

            // Act
            psVenmoController.onRefreshToken(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onRefreshToken and PSTokenization refreshToken returns Success with null and tokenizeCallback is null THEN onRefreshToken RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                id = "ID"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Success()
            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.paymentHandle = paymentHandle

            // Act
            psVenmoController.onRefreshToken(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Assert
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onRefreshToken and PSTokenization refreshToken returns Success THEN onRefreshToken RETURNS via callback onSuccess`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                id = "ID"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Success(paymentHandle)
            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback
            psVenmoController.paymentHandle = paymentHandle

            // Act
            psVenmoController.onRefreshToken(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onSuccess(any())
            }
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF onRefreshToken and PSTokenization refreshToken returns Failure and tokenizeCallback is null THEN onRefreshToken RETURNS nothing via callback`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentHandle = PaymentHandle(
                merchantRefNum = "merchantRefNum",
                paymentHandleToken = "paymentHandleToken",
                status = "",
                id = "ID"
            )
            coEvery {
                mockPSTokenizationService.refreshToken(paymentHandle)
            } returns PSResult.Failure(Exception())

            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.paymentHandle = paymentHandle

            // Act
            psVenmoController.onRefreshToken(
                ioDispatcher = UnconfinedTestDispatcher(testScheduler)
            )

            // Assert
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `test BrainTreeDetailsResponse serialization`() {
        val response = BrainTreeDetailsResponse(status = "success")
        val json = Json.encodeToString(response)
        val expectedJson = """{"status":"success"}"""
        Assert.assertEquals(expectedJson, json)
    }

    @Test
    fun `test GatewayResponseSerializable serialization`() {
        val response = GatewayResponseSerializable(
            orderId = "123",
            sessionToken = "jwt123",
            clientToken = "client123",
            processor = "processor123"
        )
        val json = Json.encodeToString(response)
        val expectedJson = """{"id":"123","sessionToken":"jwt123","clientToken":"client123","processor":"processor123"}"""
        Assert.assertEquals(expectedJson, json)
    }

    @Test
    fun `test VenmoRequestSerializable serialization`() {
        val request = VenmoRequestSerializable(consumerId = "12345")
        val json = Json.encodeToString(request)
        val expectedJson = """{"consumerId":"12345"}"""
        Assert.assertEquals(expectedJson, json)
    }

    @Test
    fun `test PaymentHandleResponseSerializable toDomain`() {
        val response = PaymentHandleResponseSerializable(
            accountId = "acc123",
            card = CardResponseSerializable("123456", NetworkTokenResponseSerializable("654321")),
            id = "id123",
            merchantRefNum = "ref123",
            paymentHandleToken = "token123",
            status = "INITIATED",
            gatewayResponse = GatewayResponseSerializable()
        )

        val domain = response.toDomain()

        Assert.assertEquals("acc123", domain.accountId)
        Assert.assertEquals("123456", domain.cardBin)
        Assert.assertEquals("654321", domain.networkTokenBin)
        Assert.assertEquals("id123", domain.id)
        Assert.assertEquals("ref123", domain.merchantRefNum)
        Assert.assertEquals("token123", domain.paymentHandleToken)
        Assert.assertEquals("INITIATED", domain.status)
        Assert.assertEquals(response.gatewayResponse, domain.gatewayResponse)
    }

    @Test
    fun `test PaymentHandleRequestSerializable toDomain`() {
        val response = PaymentHandleResponseSerializable(
            accountId = "acc123",
            card = CardResponseSerializable("123456", NetworkTokenResponseSerializable("654321")),
            id = "id123",
            merchantRefNum = "ref123",
            paymentHandleToken = "token123",
            status = "INITIATED",
            gatewayResponse = GatewayResponseSerializable()
        )

        val domain = response.toDomain()

        Assert.assertEquals("acc123", domain.accountId)
        Assert.assertEquals("123456", domain.cardBin)
        Assert.assertEquals("654321", domain.networkTokenBin)
        Assert.assertEquals("id123", domain.id)
        Assert.assertEquals("ref123", domain.merchantRefNum)
        Assert.assertEquals("token123", domain.paymentHandleToken)
        Assert.assertEquals("INITIATED", domain.status)
        Assert.assertEquals(response.gatewayResponse, domain.gatewayResponse)
    }

    @Test
    fun `test paymentHandleTokenStatusToDomain`() {
        Assert.assertEquals(PaymentHandleTokenStatus.INITIATED, paymentHandleTokenStatusToDomain("INITIATED"))
        Assert.assertEquals(PaymentHandleTokenStatus.PAYABLE, paymentHandleTokenStatusToDomain("PAYABLE"))
        Assert.assertEquals(PaymentHandleTokenStatus.EXPIRED, paymentHandleTokenStatusToDomain("EXPIRED"))
        Assert.assertEquals(PaymentHandleTokenStatus.COMPLETED, paymentHandleTokenStatusToDomain("COMPLETED"))
        Assert.assertEquals(PaymentHandleTokenStatus.PROCESSING, paymentHandleTokenStatusToDomain("PROCESSING"))
        Assert.assertEquals(PaymentHandleTokenStatus.FAILED, paymentHandleTokenStatusToDomain("UNKNOWN"))
    }

    @Test
    fun `test PaymentHandleRequestSerializable serialization`() {
        val request = PaymentHandleRequestSerializable(
            merchantRefNum = "12345",
            transactionType = TransactionTypeSerializable.PAYMENT,
            card = CardRequestSerializable(cardNum = "4111111111111111", CardExpiryRequestSerializable( month = 6, year = 24), cvv = "12/23"),
            accountId = "accountId123",
            paymentType = PaymentTypeSerializable.VENMO,
            amount = 1000,
            currencyCode = "USD",
            returnLinks = listOf(ReturnLinkSerializable(relation = ReturnLinkRelationSerializable.DEFAULT, href = "GET", method = "GET")),
            profile = ProfileSerializable(firstName = "profileId123"),
            threeDS = ThreeDSSerializable(merchantRefNum = null, merchantUrl = "merchantUrl", deviceChannel = "deviceChannel", messageCategory = MessageCategorySerializable.PAYMENT, transactionIntent = TransactionIntentSerializable.GOODS_OR_SERVICE_PURCHASE, authenticationPurpose = AuthenticationPurposeSerializable.PAYMENT_TRANSACTION, billingCycle = null, requestorChallengePreference = null,  ),
            billingDetails = BillingDetailsRequestSerializable(country = "John Doe", zip = "123 Main St"),
            merchantDescriptor = MerchantDescriptorSerializable(dynamicDescriptor = "Descriptor"),
            shippingDetails = ShippingDetailsSerializable(shipMethod = ShippingMethodSerializable.OTHER, street = null, street2 = null, city = null, state = null, countryCode = null, zip = null),
            singleUseCustomerToken = "token123",
            paymentHandleTokenFrom = "handleToken123",
            venmo = VenmoRequestSerializable(consumerId = "consumerId123")
        )
        val json = Json.encodeToString(request)
        val expectedJson = """{"merchantRefNum":"12345","transactionType":"PAYMENT","card":{"cardNum":"4111111111111111","cardExpiry":{"month":6,"year":24},"cvv":"12/23"},"accountId":"accountId123","paymentType":"VENMO","amount":1000,"currencyCode":"USD","returnLinks":[{"rel":"default","href":"GET","method":"GET"}],"profile":{"firstName":"profileId123"},"threeDs":{"merchantUrl":"merchantUrl","deviceChannel":"deviceChannel","messageCategory":"PAYMENT","transactionIntent":"GOODS_OR_SERVICE_PURCHASE","authenticationPurpose":"PAYMENT_TRANSACTION"},"billingDetails":{"country":"John Doe","zip":"123 Main St"},"merchantDescriptor":{"dynamicDescriptor":"Descriptor"},"shippingDetails":{"shipMethod":"O","street":null,"street2":null,"city":null,"state":null,"country":null,"zip":null},"singleUseCustomerToken":"token123","paymentHandleTokenFrom":"handleToken123","venmo":{"consumerId":"consumerId123"}}"""
        Assert.assertEquals(expectedJson, json)
    }

    @Test
    fun `test PaymentHandleStatusRequest serialization`() {
        val request = PaymentHandleStatusRequest(paymentHandleToken = "token123")
        val json = Json.encodeToString(request)
        val expectedJson = """{"paymentHandleToken":"token123"}"""

        Assert.assertEquals(expectedJson, json)
    }

    @Test
    fun `test PaymentHandleStatusResponse serialization`() {
        val response = PaymentHandleStatusResponse(status = "COMPLETED", paymentHandleToken = "token123")
        val json = Json.encodeToString(response)
        val expectedJson = """{"status":"COMPLETED","paymentHandleToken":"token123"}"""

        Assert.assertEquals(expectedJson, json)
    }

    @Test
    fun `TransactionType toData test`() {
        Assert.assertEquals(TransactionTypeSerializable.PAYMENT, TransactionType.PAYMENT.toData())
        Assert.assertEquals(TransactionTypeSerializable.STANDALONE_CREDIT, TransactionType.STANDALONE_CREDIT.toData())
        Assert.assertEquals(TransactionTypeSerializable.ORIGINAL_CREDIT, TransactionType.ORIGINAL_CREDIT.toData())
        Assert.assertEquals(TransactionTypeSerializable.VERIFICATION, TransactionType.VERIFICATION.toData())
    }

    @Test
    fun `ReturnLinkRelation toData test`() {
        Assert.assertEquals(ReturnLinkRelationSerializable.DEFAULT, ReturnLinkRelation.DEFAULT.toData())
        Assert.assertEquals(ReturnLinkRelationSerializable.ON_COMPLETED, ReturnLinkRelation.ON_COMPLETED.toData())
        Assert.assertEquals(ReturnLinkRelationSerializable.ON_FAILED, ReturnLinkRelation.ON_FAILED.toData())
        Assert.assertEquals(ReturnLinkRelationSerializable.ON_CANCELLED, ReturnLinkRelation.ON_CANCELLED.toData())
    }

    @Test
    fun `PaymentHandleReturnLink toData test`() {
        val domain = PaymentHandleReturnLink(ReturnLinkRelation.DEFAULT, "http://example.com", "GET")
        val expected = ReturnLinkSerializable(ReturnLinkRelationSerializable.DEFAULT, "http://example.com", "GET")
        Assert.assertEquals(expected, domain.toData())
    }

    @Test
    fun `ProfileLocale toData test`() {
        Assert.assertEquals(ProfileLocaleSerializable.CA_EN, ProfileLocale.CA_EN.toData())
        Assert.assertEquals(ProfileLocaleSerializable.EN_US, ProfileLocale.EN_US.toData())
        Assert.assertEquals(ProfileLocaleSerializable.FR_CA, ProfileLocale.FR_CA.toData())
        Assert.assertEquals(ProfileLocaleSerializable.EN_GB, ProfileLocale.EN_GB.toData())
    }

    @Test
    fun `Gender toData test`() {
        Assert.assertEquals(GenderSerializable.MALE, Gender.MALE.toData())
        Assert.assertEquals(GenderSerializable.FEMALE, Gender.FEMALE.toData())
    }

    @Test
    fun `DateOfBirth toData test`() {
        val domain = DateOfBirth(1, 2, 1990)
        val expected = DateOfBirthSerializable(1, 2, 1990)
        Assert.assertEquals(expected, domain.toData())
    }

    @Test
    fun `IdentityDocument toData test`() {
        val domain = IdentityDocument("123456789")
        val expected = IdentityDocumentSerializable("SOCIAL_SECURITY", "123456789")
        Assert.assertEquals(expected, domain.toData())
    }

    @Test
    fun `Profile toData test`() {
        val domain = Profile(
            firstName = "John",
            lastName = "Doe",
            locale = ProfileLocale.EN_US,
            merchantCustomerId = "customer123",
            dateOfBirth = DateOfBirth(1, 2, 1990),
            email = "john.doe@example.com",
            phone = "1234567890",
            mobile = "0987654321",
            gender = Gender.MALE,
            nationality = "US",
            identityDocuments = listOf(IdentityDocument("123456789"))
        )
        val expected = ProfileSerializable(
            firstName = "John",
            lastName = "Doe",
            locale = ProfileLocaleSerializable.EN_US,
            merchantCustomerId = "customer123",
            dateOfBirth = DateOfBirthSerializable(1, 2, 1990),
            email = "john.doe@example.com",
            phone = "1234567890",
            mobile = "0987654321",
            gender = GenderSerializable.MALE,
            nationality = "US",
            identityDocuments = listOf(IdentityDocumentSerializable("SOCIAL_SECURITY", "123456789"))
        )
        Assert.assertEquals(expected, domain.toData())
    }

    @Test
    fun `BillingDetails toData test`() {
        val domain = BillingDetails(
            city = "New York",
            country = "USA",
            nickName = "Home",
            state = "NY",
            street = "123 Main St",
            street1 = "Apt 1",
            street2 = "Floor 2",
            phone = "1234567890",
            zip = "10001"
        )
        val expected = BillingDetailsRequestSerializable(
            city = "New York",
            country = "USA",
            nickName = "Home",
            state = "NY",
            street = "123 Main St",
            street1 = "Apt 1",
            street2 = "Floor 2",
            phone = "1234567890",
            zip = "10001"
        )
        Assert.assertEquals(expected, domain.toData())
    }

    @Test
    fun `IF onVenmoAppNotExist THEN callback RETURNS onFailure`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)

            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.onVenmoAppNotExist()

            // Verify
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }
}