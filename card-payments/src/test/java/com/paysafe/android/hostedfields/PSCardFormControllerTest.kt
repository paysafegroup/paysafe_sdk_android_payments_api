/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.exception.genericDisplayMessage
import com.paysafe.android.core.util.LocalLog
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberView
import com.paysafe.android.hostedfields.cvv.PSCvvView
import com.paysafe.android.hostedfields.domain.model.PSCardTokenizeOptions
import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationResponse
import com.paysafe.android.hostedfields.domain.model.cardadapter.FinalizeAuthenticationResponse
import com.paysafe.android.hostedfields.domain.repository.CardAdapterAuthRepository
import com.paysafe.android.hostedfields.exception.currencyCodeInvalidIsoException
import com.paysafe.android.hostedfields.exception.improperlyCreatedMerchantAccountConfigException
import com.paysafe.android.hostedfields.exception.invalidAccountIdForPaymentMethodException
import com.paysafe.android.hostedfields.exception.invalidAccountIdParameterException
import com.paysafe.android.hostedfields.exception.noAvailablePaymentMethodsException
import com.paysafe.android.hostedfields.exception.paymentHandleCreationFailedException
import com.paysafe.android.hostedfields.exception.tokenizationAlreadyInProgressException
import com.paysafe.android.hostedfields.expirydate.PSExpiryDateTextView
import com.paysafe.android.hostedfields.expirydate.PSExpiryDateView
import com.paysafe.android.hostedfields.holdername.PSCardholderNameView
import com.paysafe.android.hostedfields.valid.ExpiryDateChecks
import com.paysafe.android.paymentmethods.PaymentMethodsServiceImpl
import com.paysafe.android.paymentmethods.domain.model.AccountConfiguration
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardTypeCategory
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod
import com.paysafe.android.paymentmethods.domain.model.PaymentMethodType
import com.paysafe.android.threedsecure.Paysafe3DS
import com.paysafe.android.threedsecure.domain.model.ThreeDSChallengePayload
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.CardRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleAction
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleTokenStatus
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PSCardFormControllerTest {

    private val correlationId = "correlationId"
    private val accountId = "1234567"
    private val currencyCode = "USD"
    private val psCardFormConfig = PSCardFormConfig(
        currencyCode = currencyCode,
        accountId = accountId
    )
    private val psCardTokenizeOptions = PSCardTokenizeOptions(
        amount = 100,
        currencyCode = currencyCode,
        transactionType = TransactionType.PAYMENT,
        merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
        accountId = accountId
    )

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var cardNumberView: PSCardNumberView

    @RelaxedMockK
    private lateinit var cardHolderNameView: PSCardholderNameView

    @RelaxedMockK
    private lateinit var cardExpiryDateView: PSExpiryDateView

    @RelaxedMockK
    private lateinit var cardCvvView: PSCvvView

    private lateinit var mockActivity: AppCompatActivity
    private lateinit var mockPSCallback: PSCallback<PSCardFormController>
    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockPSTokenizationService: PSTokenizationService
    private lateinit var mockPSResultCallback: PSResultCallback<String>
    private lateinit var psCardFormController: PSCardFormController
    private val SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE = 9014

    internal fun specifiedHostedFieldWithInvalidValueException(
        vararg fields: String,
        correlationId: String
    ) = PaysafeException(
        code = SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE,
        displayMessage = genericDisplayMessage(SPECIFIED_HOSTED_FIELD_WITH_INVALID_VALUE),
        detailedMessage = fieldsHaveInvalidValue(*fields),
        correlationId = correlationId
    )

    internal fun fieldsHaveInvalidValue(vararg fields: String) = "Fields have invalid value: ${fields.joinToString(", ")}"

    @Before
    fun setUp() {
        mockPSApiClient = mockk(relaxed = true)
        every { mockPSApiClient.getCorrelationId() } returns correlationId
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        every { PaysafeSDK.isInitialized() } returns true
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        mockkObject(PSCardFormController)
        mockActivity = Robolectric.buildActivity(AppCompatActivity::class.java).get()
        cardNumberView = PSCardNumberView(mockActivity)
        cardHolderNameView = PSCardholderNameView(mockActivity)
        cardExpiryDateView = PSExpiryDateTextView(mockActivity)
        cardCvvView = PSCvvView(mockActivity)
        mockPSCallback = mockk<PSCallback<PSCardFormController>>()
        every { mockPSCallback.onFailure(any()) } just Runs
        every { mockPSCallback.onSuccess(any()) } just Runs
        mockPSTokenizationService = mockk<PSTokenization>()
        mockPSResultCallback = mockk<PSResultCallback<String>>()
        every { mockPSResultCallback.onFailure(any()) } just Runs
        every { mockPSResultCallback.onSuccess(any()) } just Runs
        psCardFormController = spyk(
            PSCardFormController(
                cardNumberView = null,
                cardHolderNameView = null,
                cardExpiryDateView = null,
                cardCvvView = null,
                tokenizationService = mockk(),
                mainDispatcher = mockk(),
                ioDispatcher = mockk(),
                psApiClient = mockPSApiClient
            )
        )
    }

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    private fun providePSCardFormController(testDispatcher: TestDispatcher) = PSCardFormController(
        cardNumberView = cardNumberView,
        cardHolderNameView = cardHolderNameView,
        cardExpiryDateView = cardExpiryDateView,
        cardCvvView = cardCvvView,
        tokenizationService = mockPSTokenizationService,
        mainDispatcher = testDispatcher,
        ioDispatcher = testDispatcher,
        psApiClient = mockPSApiClient
    )

    @Test
    fun `IF initialize and PaysafeSDK not initialized THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            every { PaysafeSDK.isInitialized() } returns false

            // Act
            PSCardFormController.initialize(
                cardFormConfig = psCardFormConfig,
                cardNumberView = cardNumberView,
                cardHolderNameView = cardHolderNameView,
                cardExpiryDateView = cardExpiryDateView,
                cardCvvView = cardCvvView,
                callback = mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }

    @Test
    fun `IF initialize and PaysafeSDK initialized THEN internal initialize continues the flow`() =
        runTest {
            // Arrange
            justRun {
                PSCardFormController.initialize(any(), any(), any(), any(), any(), any(), any())
            }

            // Act
            PSCardFormController.initialize(
                cardFormConfig = psCardFormConfig,
                cardNumberView = cardNumberView,
                cardHolderNameView = cardHolderNameView,
                cardExpiryDateView = cardExpiryDateView,
                cardCvvView = cardCvvView,
                callback = mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                PSCardFormController.initialize(
                    cardFormConfig = psCardFormConfig,
                    cardNumberView = cardNumberView,
                    cardHolderNameView = cardHolderNameView,
                    cardExpiryDateView = cardExpiryDateView,
                    cardCvvView = cardCvvView,
                    callback = mockPSCallback,
                    dispatchers = any()
                )
            }
        }

    @Test
    fun `IF initialize with default null views and PaysafeSDK initialized THEN internal initialize continues the flow`() =
        runTest {
            // Arrange
            justRun {
                PSCardFormController.initialize(any(), any(), any(), any(), any(), any(), any())
            }

            // Act
            PSCardFormController.initialize(
                cardFormConfig = psCardFormConfig,
                callback = mockPSCallback
            )

            // Verify
            verify(exactly = 1) {
                PSCardFormController.initialize(
                    cardFormConfig = psCardFormConfig,
                    cardNumberView = null,
                    cardHolderNameView = null,
                    cardExpiryDateView = null,
                    cardCvvView = null,
                    callback = mockPSCallback,
                    dispatchers = any()
                )
            }
        }

    @Test
    fun `IF initialize and validatePaymentMethods returns Failure THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val dispatchers = Pair(testDispatcher, testDispatcher)
            coEvery {
                PSCardFormController.validatePaymentMethods(any(), any(), any())
            } returns PSResult.Failure(Exception())

            // Act
            PSCardFormController.initialize(
                cardFormConfig = psCardFormConfig,
                cardNumberView = cardNumberView,
                cardHolderNameView = cardHolderNameView,
                cardExpiryDateView = cardExpiryDateView,
                cardCvvView = cardCvvView,
                callback = mockPSCallback,
                dispatchers = dispatchers
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }

    @Test
    fun `IF initialize and validatePaymentMethods throws exception THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val dispatchers = Pair(testDispatcher, testDispatcher)
            coEvery {
                PSCardFormController.validatePaymentMethods(any(), any(), any())
            } throws Exception()

            // Act
            PSCardFormController.initialize(
                cardFormConfig = psCardFormConfig,
                cardNumberView = cardNumberView,
                cardHolderNameView = cardHolderNameView,
                cardExpiryDateView = cardExpiryDateView,
                cardCvvView = cardCvvView,
                callback = mockPSCallback,
                dispatchers = dispatchers
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onFailure(any())
            }
        }

    @Test
    fun `IF initialize and validatePaymentMethods returns Success THEN initialize RETURNS via callback onSuccess`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val dispatchers = Pair(testDispatcher, testDispatcher)
            coEvery {
                PSCardFormController.validatePaymentMethods(any(), any(), any())
            } returns PSResult.Success()

            // Act
            PSCardFormController.initialize(
                cardFormConfig = psCardFormConfig,
                cardNumberView = cardNumberView,
                cardHolderNameView = cardHolderNameView,
                cardExpiryDateView = cardExpiryDateView,
                cardCvvView = cardCvvView,
                callback = mockPSCallback,
                dispatchers = dispatchers
            )

            // Verify
            verify(exactly = 1) {
                mockPSCallback.onSuccess(any())
            }
        }

    @Test
    fun `IF validatePaymentMethods & accountId isNotAllDigits THEN validatePaymentMethods RETURNS Failure with invalidAccountIdParameterException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val invalidAccountId = "123a"

            // Act
            val result = PSCardFormController.validatePaymentMethods(
                cardFormConfig = psCardFormConfig.copy(accountId = invalidAccountId),
                psApiClient = mockPSApiClient,
                paymentMethodsService = mockPaymentMethodsService
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(invalidAccountIdParameterException(correlationId), exception)
        }

    @Test
    fun `IF validatePaymentMethods & currencyCode isNotValid THEN validatePaymentMethods RETURNS Failure with currencyCodeInvalidIsoException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val invalidCurrencyCode = "USDD"

            // Act
            val result = PSCardFormController.validatePaymentMethods(
                cardFormConfig = psCardFormConfig.copy(currencyCode = invalidCurrencyCode),
                psApiClient = mockPSApiClient,
                paymentMethodsService = mockPaymentMethodsService
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(currencyCodeInvalidIsoException(correlationId), exception)
        }

    @Test
    fun `IF validatePaymentMethods & getPaymentMethods returns Failure THEN validatePaymentMethods RETURNS Failure`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            val exceptedException = Exception()
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(any())
            } returns PSResult.Failure(exceptedException)

            // Act
            val result = PSCardFormController.validatePaymentMethods(
                cardFormConfig = psCardFormConfig,
                psApiClient = mockPSApiClient,
                paymentMethodsService = mockPaymentMethodsService
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(exceptedException, exception)
        }

    @Test
    fun `IF validatePaymentMethods & getPaymentMethods returns Success with empty list THEN validatePaymentMethods RETURNS Failure with improperlyCreatedMerchantAccountConfigException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentMethodList = listOf<PaymentMethod>()
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(any())
            } returns PSResult.Success(paymentMethodList)

            // Act
            val result = PSCardFormController.validatePaymentMethods(
                cardFormConfig = psCardFormConfig,
                psApiClient = mockPSApiClient,
                paymentMethodsService = mockPaymentMethodsService
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(improperlyCreatedMerchantAccountConfigException(correlationId), exception)
        }

    @Test
    fun `IF validatePaymentMethods & getPaymentMethods returns Success without accountConfiguration THEN validatePaymentMethods RETURNS Failure with noAvailablePaymentMethodsException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentMethodList = listOf(
                PaymentMethod(
                    accountId = accountId,
                    currencyCode = currencyCode,
                    paymentMethod = PaymentMethodType.CARD
                )
            )
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(any())
            } returns PSResult.Success(paymentMethodList)

            // Act
            val result = PSCardFormController.validatePaymentMethods(
                cardFormConfig = psCardFormConfig,
                psApiClient = mockPSApiClient,
                paymentMethodsService = mockPaymentMethodsService
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(noAvailablePaymentMethodsException(correlationId), exception)
        }

    @Test
    fun `IF validatePaymentMethods & getPaymentMethods returns Success with paymentMethod not CARD THEN validatePaymentMethods RETURNS Failure with invalidAccountIdForPaymentMethodException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentMethodList = listOf(
                PaymentMethod(
                    accountId = accountId,
                    currencyCode = currencyCode,
                    paymentMethod = PaymentMethodType.VENMO
                )
            )
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(any())
            } returns PSResult.Success(paymentMethodList)

            // Act
            val result = PSCardFormController.validatePaymentMethods(
                cardFormConfig = psCardFormConfig,
                psApiClient = mockPSApiClient,
                paymentMethodsService = mockPaymentMethodsService
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(invalidAccountIdForPaymentMethodException(correlationId), exception)
        }

    @Test
    fun `IF validatePaymentMethods & getPaymentMethods returns Success THEN validatePaymentMethods RETURNS Success`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val paymentMethodList = listOf(
                PaymentMethod(
                    accountId = accountId,
                    currencyCode = currencyCode,
                    paymentMethod = PaymentMethodType.CARD,
                    accountConfiguration = AccountConfiguration(
                        cardTypeConfig = mapOf(
                            PSCreditCardType.VISA to PSCreditCardTypeCategory.CREDIT
                        )
                    )
                )
            )
            val mockPaymentMethodsService = mockk<PaymentMethodsServiceImpl>()
            coEvery {
                mockPaymentMethodsService.getPaymentMethods(any())
            } returns PSResult.Success(paymentMethodList)

            // Act
            val result = PSCardFormController.validatePaymentMethods(
                cardFormConfig = psCardFormConfig,
                psApiClient = mockPSApiClient,
                paymentMethodsService = mockPaymentMethodsService
            )

            // Assert
            assertTrue(result is PSResult.Success)
        }

    @Test
    fun `IF dispose THEN data is cleared`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val psCardFormController = providePSCardFormController(testDispatcher)

            // Act
            psCardFormController.dispose()

            // Assert
            assertNull(psCardFormController.cardNumberView)
            assertNull(psCardFormController.cardHolderNameView)
            assertNull(psCardFormController.cardExpiryDateView)
            assertNull(psCardFormController.cardCvvView)
        }

    @Test
    fun `IF resetFields THEN each view resets it's data to default`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val psCardFormController = providePSCardFormController(testDispatcher)

            // Act
            psCardFormController.resetFields()

            // Assert
            assertEquals(cardNumberView.data, "")
            assertEquals(cardHolderNameView.data, "")
            assertEquals(cardExpiryDateView.monthData, "")
            assertEquals(cardExpiryDateView.yearData, "20")
            assertEquals(cardCvvView.data, "")
        }

    @Test
    fun `IF tokenize and tokenizationAlreadyInProgress THEN tokenize RETURNS Failure with tokenizationAlreadyInProgressException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val psCardFormController = providePSCardFormController(testDispatcher)

            // Act
            psCardFormController.tokenizationAlreadyInProgress = true
            val result = psCardFormController.tokenize(
                cardTokenizeOptions = psCardTokenizeOptions
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(tokenizationAlreadyInProgressException(correlationId), exception)
        }

    @Test
    fun `IF tokenize and tokenizationService returns Failure THEN tokenize RETURNS Failure`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val mockCardCvvView = mockk<PSCvvView>()
            every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
            every { mockCardCvvView.placeholderString } returns ""
            every { mockCardCvvView.context } returns mockActivity
            every { mockCardCvvView.isValid() } returns true
            every { mockCardCvvView.data } returns "123"
            justRun { mockCardCvvView.reset() }
            val psCardFormController = PSCardFormController(
                cardCvvView = mockCardCvvView,
                tokenizationService = mockPSTokenizationService,
                mainDispatcher = testDispatcher,
                ioDispatcher = testDispatcher,
                psApiClient = mockPSApiClient
            )
            PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
            val expectedException = Exception()
            coEvery {
                mockPSTokenizationService.tokenize(any(), any())
            } returns PSResult.Failure(expectedException)

            // Act
            val result = psCardFormController.tokenize(
                cardTokenizeOptions = psCardTokenizeOptions
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(expectedException, exception)
            assertFalse(psCardFormController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF tokenize and tokenizationService throws Exception THEN tokenize RETURNS Failure`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val mockCardCvvView = mockk<PSCvvView>()
            every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
            every { mockCardCvvView.placeholderString } returns ""
            every { mockCardCvvView.context } returns mockActivity
            every { mockCardCvvView.isValid() } returns true
            every { mockCardCvvView.data } returns "123"
            justRun { mockCardCvvView.reset() }
            val psCardFormController = PSCardFormController(
                cardCvvView = mockCardCvvView,
                tokenizationService = mockPSTokenizationService,
                mainDispatcher = testDispatcher,
                ioDispatcher = testDispatcher,
                psApiClient = mockPSApiClient
            )
            PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
            val expectedException = Exception()
            coEvery {
                mockPSTokenizationService.tokenize(any(), any())
            } throws expectedException

            // Act
            val result = psCardFormController.tokenize(
                cardTokenizeOptions = psCardTokenizeOptions
            )
            val exception = (result as PSResult.Failure).exception

            // Assert
            assertEquals(expectedException, exception)
            assertFalse(psCardFormController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF tokenize with callback and tokenizationAlreadyInProgress THEN tokenize RETURNS via callback onFailure with tokenizationAlreadyInProgressException`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val psCardFormController = providePSCardFormController(testDispatcher)

            // Act
            psCardFormController.tokenizationAlreadyInProgress = true
            psCardFormController.tokenize(
                lifecycleOwner = mockActivity,
                cardTokenizeOptions = psCardTokenizeOptions,
                callback = mockPSResultCallback
            )

            // Verify
            verify(exactly = 1) {
                mockPSResultCallback.onFailure(tokenizationAlreadyInProgressException(correlationId))
            }
            assertFalse(psCardFormController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF tokenize via callback and tokenizationService returns Failure THEN tokenize RETURNS via callback onFailure`() =
        runTest {
            // Arrange
            val testDispatcher = UnconfinedTestDispatcher(testScheduler)
            Dispatchers.setMain(testDispatcher)
            val mockCardCvvView = mockk<PSCvvView>()
            every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
            every { mockCardCvvView.placeholderString } returns ""
            every { mockCardCvvView.context } returns mockActivity
            every { mockCardCvvView.isValid() } returns true
            every { mockCardCvvView.data } returns "123"
            justRun { mockCardCvvView.reset() }
            val psCardFormController = PSCardFormController(
                cardCvvView = mockCardCvvView,
                tokenizationService = mockPSTokenizationService,
                mainDispatcher = testDispatcher,
                ioDispatcher = testDispatcher,
                psApiClient = mockPSApiClient
            )
            PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
            val expectedException = Exception()
            coEvery {
                mockPSTokenizationService.tokenize(any(), any())
            } returns PSResult.Failure(expectedException)

            // Act
            psCardFormController.tokenize(
                lifecycleOwner = mockActivity,
                cardTokenizeOptions = psCardTokenizeOptions,
                callback = mockPSResultCallback
            )

            // Verify
            verify(exactly = 1) { mockPSResultCallback.onFailure(expectedException) }
            assertFalse(psCardFormController.tokenizationAlreadyInProgress)
        }


    @Test
    fun `getContext returns context of cardCvvView when set`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val psCardFormController = providePSCardFormController(testDispatcher)

        // Act
        val context = callPrivateGetContext(psCardFormController)

        // Assert
        assertEquals(mockActivity, context)
    }

    @Test
    fun `getContext returns context of cardHolderNameView when cardCvvView is null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val psCardFormController = providePSCardFormController(testDispatcher)
        psCardFormController.cardCvvView = null

        // Act
        val context = callPrivateGetContext(psCardFormController)

        // Assert
        assertEquals(mockActivity, context)
    }

    @Test
    fun `getContext returns context of cardExpiryDateView when cardCvvView and cardHolderNameView are null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val psCardFormController = providePSCardFormController(testDispatcher)
        psCardFormController.cardCvvView = null
        psCardFormController.cardHolderNameView = null

        // Act
        val context = callPrivateGetContext(psCardFormController)

        // Assert
        assertEquals(mockActivity, context)
    }

    @Test
    fun `getContext throws exception when all views are null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val psCardFormController = providePSCardFormController(testDispatcher)
        psCardFormController.cardCvvView = null
        psCardFormController.cardHolderNameView = null
        psCardFormController.cardExpiryDateView = null

        // Act & Assert
        try {
            callPrivateGetContext(psCardFormController)
            Assert.fail("Expected a PaysafeException to be thrown")
        } catch (e: InvocationTargetException) {
            val cause = e.targetException
            assertTrue(cause is PaysafeException)
            assertEquals("PSCardFormController doesn't own any views.", (cause as PaysafeException).detailedMessage)
        }
    }

    @Test
    fun `areAllFieldsValid returns false when any field is not valid`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val psCardFormController = providePSCardFormController(testDispatcher)
        psCardFormController.cardNumberView = cardNumberView
        psCardFormController.cardHolderNameView = cardHolderNameView
        psCardFormController.cardExpiryDateView = cardExpiryDateView
        psCardFormController.cardCvvView = cardCvvView

        // Act
        val result = psCardFormController.areAllFieldsValid()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `areAllFieldsValid returns false when views are null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        val psCardFormController = providePSCardFormController(testDispatcher)
        psCardFormController.cardNumberView = null
        psCardFormController.cardHolderNameView = null
        psCardFormController.cardExpiryDateView = null
        psCardFormController.cardCvvView = null

        // Act
        val result = psCardFormController.areAllFieldsValid()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `tokenize returns Success when tokenizeResult is Success`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.PAYABLE.status,
            action = PaymentHandleAction.NONE.toString()
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock other dependencies
        coEvery {mockPSTokenizationService.refreshToken(paymentHandle)} returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Success)
        assertEquals("token", (result as PSResult.Success).value)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns Success when action is UNKNOWN and status is PAYABLE`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.PAYABLE.status,
            action = "UNKNOWN"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock other dependencies
        coEvery {mockPSTokenizationService.refreshToken(paymentHandle)} returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Success)
        assertEquals("token", (result as PSResult.Success).value)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns Success when action is UNKNOWN and status is COMPLETED`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.COMPLETED.status,
            action = "UNKNOWN"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock other dependencies
        coEvery {mockPSTokenizationService.refreshToken(paymentHandle)} returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Success)
        assertEquals("token", (result as PSResult.Success).value)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `On refresh token returns failure when payment handle is null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.FAILED.status,
            action = PaymentHandleAction.NONE.toString()
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
    }

    @Test
    fun `tokenize returns Failure when paymentHandle id is null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = null,
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.FAILED.status,
            action = PaymentHandleAction.NONE.toString()
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns Failure when paymentHandle is null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = null
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns Failure when deviceFingerprint is null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.FAILED.status,
            action = PaymentHandleAction.NONE.toString()
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock other dependencies
        val mockPaysafe3DS = mockk<Paysafe3DS>()
        coEvery { mockPaysafe3DS.start(any(), any(), any(), any()) } returns PSResult.Success(null)
        mockkConstructor(Paysafe3DS::class)
        coEvery { anyConstructed<Paysafe3DS>().start(any(), any(), any(), any()) } returns PSResult.Success(null)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns Failure when authentication status is FAILED`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.INITIATED.status,
            action = PaymentHandleAction.REDIRECT.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock other dependencies
        val mockPaysafe3DS = mockk<Paysafe3DS>()
        coEvery { mockPaysafe3DS.start(any(), any(), any(), any()) } returns PSResult.Success("deviceFingerprint")
        mockkConstructor(Paysafe3DS::class)
        coEvery { anyConstructed<Paysafe3DS>().start(any(), any(), any(), any()) } returns PSResult.Success("deviceFingerprint")

        // Mock CardAdapterAuthRepository
        val mockCardAdapterAuthRepository = mockk<CardAdapterAuthRepository>()
        val authenticationResponse = AuthenticationResponse(
            status = AuthenticationStatus.FAILED,
            sdkChallengePayload = null
        )
        coEvery {
            mockCardAdapterAuthRepository.startAuthentication(any(), any())
        } returns PSResult.Success(authenticationResponse)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = AuthenticationStatus.FAILED.value,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Replace the repository in the controller
        setPrivateProperty(psCardFormController, "cardAdapterAuthRepository", mockCardAdapterAuthRepository)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `continueAuthenticationFlow returns FinalizeAuthenticationResponse when challenge is completed when status is INITIATED`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.INITIATED.status,
            action = PaymentHandleAction.REDIRECT.toString(),
            cardBin = "1234"
        )
        val mockPaysafe3DS = mockk<Paysafe3DS>()
        coEvery { mockPaysafe3DS.launch3dsChallenge(any(), any()) } returns PSResult.Success(
            ThreeDSChallengePayload(authenticationId = "authId")
        )
        mockkConstructor(Paysafe3DS::class)
        coEvery { anyConstructed<Paysafe3DS>().launch3dsChallenge(any(), any()) } returns PSResult.Success(
            ThreeDSChallengePayload(authenticationId = "authId")
        )
        val mockCardAdapterAuthRepository = mockk<CardAdapterAuthRepository>()
        coEvery {
            mockCardAdapterAuthRepository.finalizeAuthentication(any(), any())
        } returns PSResult.Success(FinalizeAuthenticationResponse(status = AuthenticationStatus.COMPLETED))

        // Use reflection to set private field
        setPrivateProperty(psCardFormController, "cardAdapterAuthRepository", mockCardAdapterAuthRepository)

        // Act
        val result = callPrivateContinueAuthenticationFlow(
            psCardFormController,
            mockActivity,
            mockPaysafe3DS,
            "challengePayload",
            paymentHandle.id!!
        )

        // Assert
        assertNotNull(result)
        assertEquals(AuthenticationStatus.COMPLETED, result?.status)
    }

    @Test
    fun `continueAuthenticationFlow returns FinalizeAuthenticationResponse when challenge is completed when status is PROCESSING`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.PROCESSING.status,
            action = PaymentHandleAction.REDIRECT.toString(),
            cardBin = "1234"
        )
        val mockPaysafe3DS = mockk<Paysafe3DS>()
        coEvery { mockPaysafe3DS.launch3dsChallenge(any(), any()) } returns PSResult.Success(
            ThreeDSChallengePayload(authenticationId = "authId")
        )
        mockkConstructor(Paysafe3DS::class)
        coEvery { anyConstructed<Paysafe3DS>().launch3dsChallenge(any(), any()) } returns PSResult.Success(
            ThreeDSChallengePayload(authenticationId = "authId")
        )
        val mockCardAdapterAuthRepository = mockk<CardAdapterAuthRepository>()
        coEvery {
            mockCardAdapterAuthRepository.finalizeAuthentication(any(), any())
        } returns PSResult.Success(FinalizeAuthenticationResponse(status = AuthenticationStatus.COMPLETED))

        // Use reflection to set private field
        setPrivateProperty(psCardFormController, "cardAdapterAuthRepository", mockCardAdapterAuthRepository)

        // Act
        val result = callPrivateContinueAuthenticationFlow(
            psCardFormController,
            mockActivity,
            mockPaysafe3DS,
            "challengePayload",
            paymentHandle.id!!
        )

        // Assert
        assertNotNull(result)
        assertEquals(AuthenticationStatus.COMPLETED, result?.status)
    }

    @Test
    fun `tokenize returns PSResult Success when status is COMPLETED and action is NONE`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.COMPLETED.status,
            action = PaymentHandleAction.NONE.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Success)
        assertEquals("token", (result as PSResult.Success).value)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Failure when status is PROCESSING and action is NONE`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.PROCESSING.status,
            action = PaymentHandleAction.NONE.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = PaymentHandleTokenStatus.PROCESSING.status,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Failure when status is INITIATED and action is NONE`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.INITIATED.status,
            action = PaymentHandleAction.NONE.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = PaymentHandleTokenStatus.INITIATED.status,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Success when status is PAYABLE and action is REDIRECT`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.PAYABLE.status,
            action = PaymentHandleAction.REDIRECT.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock other dependencies
        coEvery {mockPSTokenizationService.refreshToken(paymentHandle)} returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Success)
        assertEquals("token", (result as PSResult.Success).value)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Success when status is COMPLETED and action is REDIRECT`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.COMPLETED.status,
            action = PaymentHandleAction.REDIRECT.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock other dependencies
        coEvery {mockPSTokenizationService.refreshToken(paymentHandle)} returns PSResult.Success(paymentHandle)

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Success)
        assertEquals("token", (result as PSResult.Success).value)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Failure when status is FAILED and action is REDIRECT`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.FAILED.status,
            action = PaymentHandleAction.REDIRECT.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = PaymentHandleTokenStatus.FAILED.status,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Failure when status is EXPIRED and action is REDIRECT`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.EXPIRED.status,
            action = PaymentHandleAction.REDIRECT.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = PaymentHandleTokenStatus.EXPIRED.status,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Failure when status is EXPIRED and action is UNKNOWN`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.EXPIRED.status,
            action = "UNKNOWN",
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = PaymentHandleTokenStatus.EXPIRED.status,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns PSResult Failure when status is FAILED and action is UNKNOWN`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.FAILED.status,
            action = "UNKNOWN",
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = PaymentHandleTokenStatus.FAILED.status,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns Failure when status is EXPIRED and action in NONE`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.EXPIRED.status,
            action = PaymentHandleAction.NONE.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = PaymentHandleTokenStatus.EXPIRED.status,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `tokenize returns Failure when status is FAILED and action in NONE`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardCvvView = mockk<PSCvvView>()
        every { mockCardCvvView.cardType } returns PSCreditCardType.VISA
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity
        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.data } returns "123"
        justRun { mockCardCvvView.reset() }
        val psCardFormController = PSCardFormController(
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )
        PSCardFormController.supportedCardTypes = listOf(PSCreditCardType.VISA)
        val paymentHandle = PaymentHandle(
            id = "id",
            paymentHandleToken = "token",
            merchantRefNum = "refNum",
            status = PaymentHandleTokenStatus.FAILED.status,
            action = PaymentHandleAction.NONE.toString(),
            cardBin = "1234"
        )
        coEvery {
            mockPSTokenizationService.tokenize(any(), any())
        } returns PSResult.Success(paymentHandle)

        // Mock handleAuthenticationStatusFailed()
        val expectedException = paymentHandleCreationFailedException(
            status = AuthenticationStatus.FAILED.value,
            correlationId = correlationId
        )
        every { mockPSApiClient.logErrorEvent(any(), any()) } just Runs

        // Act
        val result = psCardFormController.tokenize(
            cardTokenizeOptions = psCardTokenizeOptions
        )

        // Assert
        assertTrue(result is PSResult.Failure)
        val failureResult = result as PSResult.Failure
        assertTrue(failureResult.exception is PaysafeException)
        assertEquals(expectedException, failureResult.exception)
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `getCardRequestData sets cardExpiration when cardExpiryDateView is not null`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)
        val mockCardNumberView = mockk<PSCardNumberView>()
        val mockCardHolderNameView = mockk<PSCardholderNameView>()
        val mockCardExpiryDateView = mockk<PSExpiryDateView>()
        val mockCardCvvView = mockk<PSCvvView>()

        every { mockCardNumberView.isValid() } returns true
        every { mockCardNumberView.isValidLiveData } returns MutableLiveData(true)
        every { mockCardNumberView.data } returns "4111111111111111"
        every { mockCardNumberView.cardTypeLiveData } returns MutableLiveData(PSCreditCardType.UNKNOWN)
        every { mockCardNumberView.placeholderString } returns ""
        every { mockCardNumberView.context } returns mockActivity

        every { mockCardHolderNameView.isValid() } returns true
        every { mockCardHolderNameView.isValidLiveData } returns MutableLiveData(true)
        every { mockCardHolderNameView.data } returns "John Doe"
        every { mockCardHolderNameView.placeholderString } returns ""
        every { mockCardHolderNameView.context } returns mockActivity

        every { mockCardExpiryDateView.monthData } returns "12"
        every { mockCardExpiryDateView.yearData } returns "2030"
        every { mockCardExpiryDateView.isValidLiveData } returns MutableLiveData(true)
        every { mockCardExpiryDateView.placeholderString } returns ""
        every { mockCardExpiryDateView.viewContext } returns mockActivity

        every { mockCardCvvView.isValid() } returns true
        every { mockCardCvvView.isValidLiveData } returns MutableLiveData(true)
        every { mockCardCvvView.data } returns "123"
        every { mockCardCvvView.placeholderString } returns ""
        every { mockCardCvvView.context } returns mockActivity

        mockkObject(ExpiryDateChecks)
        every { ExpiryDateChecks.validations(any()) } returns false

        val psCardFormController = PSCardFormController(
            cardNumberView = mockCardNumberView,
            cardHolderNameView = mockCardHolderNameView,
            cardExpiryDateView = mockCardExpiryDateView,
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )

        // Act
        val cardRequest = callPrivateGetCardRequestData(psCardFormController)
        val hostedFieldsErrors = callPrivateHostedFieldsErrorsList(psCardFormController)

        // Assert
        assertNotNull(cardRequest.cardExpiry)
        assertEquals("12", cardRequest.cardExpiry?.month)
        assertEquals("2030", cardRequest.cardExpiry?.year)
        assertTrue(hostedFieldsErrors.isEmpty())
    }

    @Test
    fun `onCardBrandRecognition is invoked and cardType is set on cardCvvView`() = runTest {
        // Arrange
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        Dispatchers.setMain(testDispatcher)

        val mockActivity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()

        // Mock views
        val mockCardNumberView = mockk<PSCardNumberView>(relaxed = true) {
            every { isValid() } returns true
            every { isValidLiveData } returns MutableLiveData(true)
            every { cardTypeLiveData } returns MutableLiveData(PSCreditCardType.VISA)
            every { context } returns mockActivity
        }
        val mockCardCvvView = mockk<PSCvvView>(relaxed = true)

        val psCardFormController = PSCardFormController(
            cardNumberView = mockCardNumberView,
            cardHolderNameView = mockk(relaxed = true),
            cardExpiryDateView = mockk(relaxed = true),
            cardCvvView = mockCardCvvView,
            tokenizationService = mockPSTokenizationService,
            mainDispatcher = testDispatcher,
            ioDispatcher = testDispatcher,
            psApiClient = mockPSApiClient
        )

        val onCardBrandRecognition: (PSCreditCardType) -> Unit = mockk(relaxed = true)
        psCardFormController.onCardBrandRecognition = onCardBrandRecognition

        // Act
        (mockCardNumberView.cardTypeLiveData as MutableLiveData).value = PSCreditCardType.VISA

        // Assert
        verify { onCardBrandRecognition.invoke(PSCreditCardType.VISA) }
        verify { mockCardCvvView.cardType = PSCreditCardType.VISA }
    }

    @Test
    fun `refreshTokenFailureHandler returns PSResult Failure with PaysafeException`() {
        // Arrange
        val exception = mockk<PaysafeException>(relaxed = true)
        val failureResult = PSResult.Failure(exception)
        every { mockPSApiClient.getCorrelationId() } returns "correlationId"

        // Act
        val result = callPrivateRefreshTokenFailureHandler(psCardFormController, failureResult)

        // Assert
        assertTrue(result.exception is PaysafeException)
        verify { LocalLog.d("PSCardFormController", "Refresh token failed with ${exception.message}") }
        assertFalse(psCardFormController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `refreshTokenFailureHandler returns PSResult Failure with generic exception`() {
        // Arrange
        val exception = Exception("Generic error")
        val failureResult = PSResult.Failure(exception)
        every { mockPSApiClient.getCorrelationId() } returns "correlationId"
        justRun { mockPSApiClient.logErrorEvent(any(), any()) }

        // Act
        val result = callPrivateRefreshTokenFailureHandler(psCardFormController, failureResult)

        // Assert
        assertTrue(result.exception is PaysafeException)
    }

    @Test
    fun `refreshTokenSuccessHandler returns PSResult Failure when refreshed PaymentHandle is null`() {
        // Arrange
        val refreshTokenResult = PSResult.Success<PaymentHandle>(null)
        val mockPSApiClient = mockk<PSApiClient>()
        every { mockPSApiClient.getCorrelationId() } returns "correlationId"
        justRun { mockPSApiClient.logErrorEvent(any(), any()) }

        val psCardFormController = spyk(
            PSCardFormController(
                cardCvvView = mockk(relaxed = true),
                tokenizationService = mockk(relaxed = true),
                mainDispatcher = Dispatchers.Unconfined,
                ioDispatcher = Dispatchers.Unconfined,
                psApiClient = mockPSApiClient
            )
        )

        // Act
        val result = callPrivateRefreshTokenSuccessHandler(psCardFormController, refreshTokenResult)

        // Assert
        assertTrue(result is PSResult.Failure)
        if (result is PSResult.Failure) {
            assertTrue(result.exception is PaysafeException)
            val paysafeException = result.exception as PaysafeException
            assertEquals(9014, paysafeException.code)
            assertEquals("There was an error (9014), please contact our support.", paysafeException.displayMessage)
            assertEquals("Unhandled error occurred.", paysafeException.detailedMessage)
            assertEquals("correlationId", paysafeException.correlationId)
        }
    }


    // Helper function to call the private method via reflection
    private fun callPrivateRefreshTokenSuccessHandler(
        controller: PSCardFormController,
        refreshTokenResult: PSResult.Success<PaymentHandle>
    ): PSResult<String> {
        val method = PSCardFormController::class.java.getDeclaredMethod(
            "refreshTokenSuccessHandler", PSResult.Success::class.java
        )
        method.isAccessible = true
        return method.invoke(controller, refreshTokenResult) as PSResult<String>
    }

    private fun callPrivateGetContext(controller: PSCardFormController): Context {
        val method = PSCardFormController::class.java.getDeclaredMethod("getContext")
        method.isAccessible = true
        return method.invoke(controller) as Context
    }

    private fun setPrivateProperty(obj: Any, propertyName: String, value: Any) {
        obj.javaClass.getDeclaredField(propertyName).apply {
            isAccessible = true
            set(obj, value)
        }
    }

    private suspend fun callPrivateContinueAuthenticationFlow(
        controller: PSCardFormController,
        activity: Activity,
        paysafe3DS: Paysafe3DS,
        sdkChallengePayload: String,
        paymentHandleId: String
    ): FinalizeAuthenticationResponse? {
        val method = PSCardFormController::class.declaredFunctions
            .first { it.name == "continueAuthenticationFlow" }
        method.isAccessible = true

        return method.callSuspend(controller, activity, paysafe3DS, sdkChallengePayload, paymentHandleId) as FinalizeAuthenticationResponse?
    }

    private suspend fun callPrivateGetCardRequestData(controller: PSCardFormController): CardRequest {
        val method = PSCardFormController::class.declaredFunctions
            .first { it.name == "getCardRequestData" }
        method.isAccessible = true

        return method.callSuspend(controller) as CardRequest
    }

    private suspend fun callPrivateHostedFieldsErrorsList(controller: PSCardFormController): List<String> {
        val method = PSCardFormController::class.declaredFunctions
            .first { it.name == "hostedFieldsErrorsList" }
        method.isAccessible = true

        return method.callSuspend(controller) as List<String>
    }
    private fun callPrivateRefreshTokenFailureHandler(
        controller: PSCardFormController,
        refreshTokenResult: PSResult.Failure
    ): PSResult.Failure {
        val method: Method = PSCardFormController::class.java.getDeclaredMethod(
            "refreshTokenFailureHandler",
            PSResult.Failure::class.java
        )
        method.isAccessible = true
        return method.invoke(controller, refreshTokenResult) as PSResult.Failure
    }



}