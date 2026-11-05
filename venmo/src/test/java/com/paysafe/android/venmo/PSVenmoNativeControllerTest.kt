package com.paysafe.android.venmo

import android.content.Context
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.lifecycleScope
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.brainTreeDetails.domain.models.DeviceData
import com.paysafe.android.brainTreeDetails.domain.models.PayerInfo
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.tokenization.data.entity.paymentHandle.GatewayResponseSerializable
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandle
import com.paysafe.android.venmo.activity.VenmoWebCheckoutActivityResult
import com.paysafe.android.venmo.data.DeviceDataCollector
import com.paysafe.android.venmo.domain.model.BraintreeDetailsConfiguration
import com.paysafe.android.venmo.domain.model.VenmoWebCheckoutConfiguration
import com.paysafe.android.venmo.exception.errorName
import com.paysafe.android.venmo.exception.genericApiErrorException
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PSVenmoNativeControllerTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val sessionToken = "SESSION_TOKEN_MOCK"
    private val clientToken = "CLIENT_TOKEN_MOCK"
    private val customUrlScheme = "CUSTOM_URL_SCHEME_MOCK"
    private val appLinkReturnUrl = "https://merchant.example.com"
    private val orderId = "ORDER_ID_MOCK"
    private val correlationId = "testCorrelationId"


    private lateinit var mockActivity: AppCompatActivity
    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockActivityResultCaller: ActivityResultCaller
    private lateinit var mockPSTokenizationService: PSTokenizationService
    private lateinit var mockPSVenmoTokenizeCallback: PSVenmoTokenizeCallback

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
        tokenizationService = mockPSTokenizationService
    )

    @Test
    fun `IF dispose THEN launcher callback and checkout state are cleared`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()
            psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback
            psVenmoController.paymentHandle = paymentHandle()

            // Act
            psVenmoController.dispose()

            // Verify
            verify(exactly = 1) {
                psVenmoController.activityResultLauncher.unregister()
            }
            Assert.assertNull(psVenmoController.tokenizeCallback)
            Assert.assertNull(psVenmoController.paymentHandle)
            Assert.assertNull(psVenmoController.lifecycleScopeWeakRef.get())
        }

    @Test
    fun `IF dispose and lifecycle scope is already cleared THEN launcher is still unregistered`() {
        val psVenmoController = providePSVenmoNativeController()
        psVenmoController.lifecycleScopeWeakRef.clear()

        psVenmoController.dispose()

        verify(exactly = 1) {
            psVenmoController.activityResultLauncher.unregister()
        }
        Assert.assertNull(psVenmoController.lifecycleScopeWeakRef.get())
    }

    @Test
    fun `IF startVenmoCheckout THEN collected device data is launched via activityResultLauncher`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()
            val deviceData = """{"correlation_id":"corr-1"}"""
            val clientMetadataId = "meta-123"
            collectedMetadataId = null
            collectedAuthorization = null
            psVenmoController.deviceDataCollector = collectorReturning(deviceData)

            // Act
            psVenmoController.startVenmoCheckout(
                context = mockActivity,
                orderId = orderId,
                sessionToken = sessionToken,
                clientToken = clientToken,
                profileId = "profile-id",
                amount = 1000,
                appLinkReturnUrl = appLinkReturnUrl,
                customUrlScheme = customUrlScheme,
                clientMetadataId = clientMetadataId
            )

            // Verify
            Assert.assertEquals(clientMetadataId, collectedMetadataId)
            Assert.assertEquals(clientToken, collectedAuthorization)
            verify(exactly = 0) { mockPSApiClient.logErrorEvent(any(), any()) }
            verify(exactly = 1) {
                psVenmoController.activityResultLauncher.launch(
                    VenmoWebCheckoutConfiguration(
                        orderId = orderId,
                        sessionToken = sessionToken,
                        clientToken = clientToken,
                        profileId = "profile-id",
                        amount = "10.0",
                        appLinkReturnUrl = appLinkReturnUrl,
                        customUrlScheme = customUrlScheme,
                        clientMetadataId = clientMetadataId,
                        deviceDataCorrelationId = deviceData
                    )
                )
            }
        }

    @Test
    fun `IF startVenmoCheckout has no optional checkout fields THEN those extras are omitted`() {
        val psVenmoController = providePSVenmoNativeController()
        collectedMetadataId = "stale"
        psVenmoController.deviceDataCollector = collectorReturning("""{"correlation_id":"corr-1"}""")

        psVenmoController.startVenmoCheckout(
            context = mockActivity,
            orderId = orderId,
            sessionToken = sessionToken,
            clientToken = clientToken,
            profileId = null,
            amount = 250,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = null,
            clientMetadataId = null
        )

        Assert.assertNull(collectedMetadataId)
        verify(exactly = 1) {
            psVenmoController.activityResultLauncher.launch(
                withArg { configuration ->
                    Assert.assertNull(configuration.profileId)
                    Assert.assertNull(configuration.customUrlScheme)
                    Assert.assertNull(configuration.clientMetadataId)
                    Assert.assertEquals("2.5", configuration.amount)
                    Assert.assertEquals(
                        """{"correlation_id":"corr-1"}""",
                        configuration.deviceDataCorrelationId
                    )
                }
            )
        }
    }

    @Test
    fun `IF startVenmoCheckout collects blank device data THEN the checkout intent omits it`() {
        val psVenmoController = providePSVenmoNativeController()
        psVenmoController.deviceDataCollector = collectorReturning("   ")

        psVenmoController.startVenmoCheckout(
            context = mockActivity,
            orderId = orderId,
            sessionToken = sessionToken,
            clientToken = clientToken,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            clientMetadataId = "meta-123"
        )

        verify(exactly = 0) { mockPSApiClient.logErrorEvent(any(), any()) }
        verify(exactly = 1) {
            psVenmoController.activityResultLauncher.launch(
                withArg { configuration ->
                    Assert.assertNull(configuration.deviceDataCorrelationId)
                    Assert.assertEquals(sessionToken, configuration.sessionToken)
                }
            )
        }
    }

    @Test
    fun `IF startVenmoCheckout device data collection fails THEN the error is logged and checkout continues`() {
        val psVenmoController = providePSVenmoNativeController()
        val error = IllegalStateException("collection failed")
        collectedMetadataId = null
        collectedAuthorization = null
        psVenmoController.deviceDataCollector =
            DeviceDataCollector { _, authorization, clientMetadataId, callback ->
                collectedAuthorization = authorization
                collectedMetadataId = clientMetadataId
                callback(Result.failure(error))
            }

        psVenmoController.startVenmoCheckout(
            context = mockActivity,
            orderId = orderId,
            sessionToken = sessionToken,
            clientToken = clientToken,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            clientMetadataId = "meta-123"
        )

        Assert.assertEquals("meta-123", collectedMetadataId)
        Assert.assertEquals(clientToken, collectedAuthorization)
        verify(exactly = 1) {
            mockPSApiClient.logErrorEvent(
                "collection failed",
                withArg { exception ->
                    Assert.assertEquals(correlationId, exception.correlationId)
                    Assert.assertTrue(exception.displayMessage.contains("collection failed"))
                }
            )
        }
        verify(exactly = 1) {
            psVenmoController.activityResultLauncher.launch(
                withArg { configuration ->
                    Assert.assertNull(configuration.deviceDataCorrelationId)
                    Assert.assertEquals("meta-123", configuration.clientMetadataId)
                }
            )
        }
    }

    @Test
    fun `IF handleActivityResult with RESULT_CANCELED THEN callback RETURNS onFailure`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback
            psVenmoController.tokenizationAlreadyInProgress = true
            psVenmoController.handleActivityResult(VenmoWebCheckoutActivityResult.Canceled)

            // Verify
            val expected = genericApiErrorException(correlationId)
            verify { mockPSApiClient.logErrorEvent(expected.errorName(), expected) }
            verify { mockPSVenmoTokenizeCallback.onFailure(expected) }
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF the registered activity result arrives THEN it is handled as a Venmo result`() {
        val resultCallback = slot<ActivityResultCallback<VenmoWebCheckoutActivityResult>>()
        every {
            mockActivityResultCaller.registerForActivityResult(
                any<ActivityResultContract<VenmoWebCheckoutConfiguration, VenmoWebCheckoutActivityResult>>(),
                capture(resultCallback)
            )
        } returns mockk(relaxed = true)
        val psVenmoController = providePSVenmoNativeController()
        psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback
        psVenmoController.tokenizationAlreadyInProgress = true

        resultCallback.captured.onActivityResult(VenmoWebCheckoutActivityResult.Canceled)

        verify {
            mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
        }
        Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `IF handleActivityResult with RESULT_SUCCESS THEN details include the Venmo account`() {
        val psVenmoController = spyk(providePSVenmoNativeController())
        val deviceData = """{"correlation_id":"corr-1"}"""
        val details = BraintreeDetailsConfiguration(
            account = "nonce",
            userName = "venmo-user",
            firstName = "Ada",
            lastName = "Lovelace",
            phoneNumber = "555",
            email = "ada@example.com",
            externalId = "external-id",
            sessionToken = sessionToken,
            deviceDataCorrelationId = deviceData
        )
        justRun { psVenmoController.onVenmoListenerSuccess(any()) }

        psVenmoController.handleActivityResult(VenmoWebCheckoutActivityResult.Success(details))

        verify {
            psVenmoController.onVenmoListenerSuccess(
                withArg { request ->
                    Assert.assertEquals("nonce", request.paymentMethodNonce)
                    Assert.assertEquals(sessionToken, request.paymentMethodJwtToken)
                    Assert.assertEquals(DeviceData(correlationId = deviceData), request.paymentMethodDeviceData)
                    Assert.assertEquals(
                        PayerInfo(
                            firstName = "Ada",
                            lastName = "Lovelace",
                            phoneNumber = "555",
                            email = "ada@example.com",
                            externalId = "external-id",
                            userName = "venmo-user"
                        ),
                        request.paymentMethodPayerInfo
                    )
                    Assert.assertNull(request.errorCode)
                }
            )
        }
    }

    @Test
    fun `IF handleActivityResult is Failure THEN details are marked canceled`() {
        val psVenmoController = spyk(providePSVenmoNativeController())
        justRun { psVenmoController.onVenmoFailure(any()) }

        psVenmoController.handleActivityResult(VenmoWebCheckoutActivityResult.Failure)

        verify {
            psVenmoController.onVenmoFailure(
                withArg { request ->
                    Assert.assertEquals("VENMO_CANCELED", request.errorCode)
                    Assert.assertEquals("", request.paymentMethodNonce)
                    Assert.assertNull(request.paymentMethodDeviceData)
                }
            )
        }
    }

    @Test
    fun `IF provideController THEN PSVenmoNativeController is created`() {
        // Arrange

        // Act
        val psVenmoController = PSVenmoNativeController.provideController(
            activityResultCaller = mockActivityResultCaller,
            lifecycleScope = mockActivity.lifecycleScope,
            psApiClient = mockPSApiClient,
        )

        // Assert
        assertNotNull(psVenmoController)
    }

    @Test
    fun `IF createRequest has collected device data THEN braintree details receives that payload`() {
        val psVenmoController = providePSVenmoNativeController()
        val deviceData = """{"correlation_id":"bt-correlation"}"""
        val data = BraintreeDetailsConfiguration(
            account = "nonce",
            firstName = null,
            lastName = null,
            phoneNumber = null,
            email = null,
            userName = null,
            externalId = null,
            deviceDataCorrelationId = deviceData,
            sessionToken = sessionToken
        )

        val request = psVenmoController.createRequest(data)

        Assert.assertEquals("nonce", request.paymentMethodNonce)
        Assert.assertEquals(DeviceData(correlationId = deviceData), request.paymentMethodDeviceData)
        Assert.assertEquals(sessionToken, request.paymentMethodJwtToken)
        Assert.assertEquals(
            PayerInfo(
                firstName = "",
                lastName = "",
                phoneNumber = "",
                email = "",
                userName = "",
                externalId = ""
            ),
            request.paymentMethodPayerInfo
        )
        Assert.assertNull(request.errorCode)
    }

    @Test
    fun `IF createRequest has no session token THEN the checkout session token is used`() {
        val psVenmoController = providePSVenmoNativeController()
        psVenmoController.deviceDataCollector = collectorReturning("""{"correlation_id":"corr-1"}""")
        psVenmoController.startVenmoCheckout(
            context = mockActivity,
            orderId = orderId,
            sessionToken = sessionToken,
            clientToken = clientToken,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            clientMetadataId = null
        )
        val data = BraintreeDetailsConfiguration(
            account = "nonce",
            firstName = "Ada",
            lastName = null,
            phoneNumber = null,
            email = null,
            userName = null,
            externalId = null,
            deviceDataCorrelationId = null,
            sessionToken = null
        )

        val request = psVenmoController.createRequest(data)

        Assert.assertEquals(sessionToken, request.paymentMethodJwtToken)
        Assert.assertEquals("nonce", request.paymentMethodNonce)
        Assert.assertEquals("Ada", request.paymentMethodPayerInfo?.firstName)
        Assert.assertNull(request.paymentMethodDeviceData)
        Assert.assertNull(request.errorCode)
    }

    @Test
    fun `IF createRequest has no Venmo result THEN failure code is sent and device data is omitted`() {
        val psVenmoController = providePSVenmoNativeController()

        val request = psVenmoController.createRequest(data = null)

        Assert.assertNull(request.paymentMethodDeviceData)
        Assert.assertEquals("VENMO_CANCELED", request.errorCode)
    }

    @Test
    fun `IF recurring shopper THEN Venmo is not opened and details contain session token and device data`() {
        val psVenmoController = spyk(providePSVenmoNativeController())
        val deviceData = """{"correlation_id":"corr-1"}"""
        collectedMetadataId = null
        psVenmoController.deviceDataCollector = collectorReturning(deviceData)
        justRun { psVenmoController.onVenmoListenerSuccess(any()) }

        psVenmoController.handleTokenizeResultSuccess(
            context = mockActivity,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            result = PSResult.Success(
                paymentHandle(
                    brainTreeCustomerId = "customer-1",
                    clientMetadataId = "meta-123",
                    action = "REDIRECT"
                )
            )
        )

        verify(exactly = 0) { psVenmoController.activityResultLauncher.launch(any()) }
        verify {
            psVenmoController.onVenmoListenerSuccess(
                withArg { request ->
                    Assert.assertEquals(sessionToken, request.paymentMethodJwtToken)
                    Assert.assertEquals(
                        DeviceData(correlationId = deviceData),
                        request.paymentMethodDeviceData
                    )
                    Assert.assertNull(request.paymentMethodNonce)
                    Assert.assertNull(request.paymentMethodPayerInfo)
                }
            )
        }
        Assert.assertEquals("meta-123", collectedMetadataId)
    }

    @Test
    fun `IF first time redirect THEN Venmo opens with the collected device data`() {
        val psVenmoController = providePSVenmoNativeController()
        val deviceData = """{"correlation_id":"corr-1"}"""
        val preferences = mockActivity.getSharedPreferences(
            "venmo_pending_request_prefs",
            Context.MODE_PRIVATE
        )
        preferences.edit().remove("venmo_device_data").commit()
        collectedMetadataId = null
        psVenmoController.tokenizationAlreadyInProgress = true
        psVenmoController.deviceDataCollector = collectorReturning(deviceData)

        psVenmoController.handleTokenizeResultSuccess(
            context = mockActivity,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            result = PSResult.Success(paymentHandle(action = "REDIRECT"))
        )

        verify {
            psVenmoController.activityResultLauncher.launch(
                withArg { configuration ->
                    Assert.assertEquals(deviceData, configuration.deviceDataCorrelationId)
                    Assert.assertEquals(orderId, configuration.orderId)
                }
            )
        }
        Assert.assertNull(collectedMetadataId)
        Assert.assertNull(preferences.getString("venmo_device_data", null))
        Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
    }

    @Test
    fun `IF first time redirect has client metadata id THEN it is used to collect device data`() {
        val psVenmoController = providePSVenmoNativeController()
        val deviceData = """{"correlation_id":"corr-1"}"""
        collectedMetadataId = null
        collectedAuthorization = null
        psVenmoController.deviceDataCollector = collectorReturning(deviceData)

        psVenmoController.handleTokenizeResultSuccess(
            context = mockActivity,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            result = PSResult.Success(
                paymentHandle(action = "REDIRECT", clientMetadataId = "meta-123")
            )
        )

        Assert.assertEquals("meta-123", collectedMetadataId)
        Assert.assertEquals(clientToken, collectedAuthorization)
        verify {
            psVenmoController.activityResultLauncher.launch(
                withArg { configuration ->
                    Assert.assertEquals(deviceData, configuration.deviceDataCorrelationId)
                    Assert.assertEquals("meta-123", configuration.clientMetadataId)
                    Assert.assertEquals(clientToken, configuration.clientToken)
                }
            )
        }
    }

    @Test
    fun `IF action is not REDIRECT THEN Venmo is not opened`() {
        val psVenmoController = providePSVenmoNativeController()
        psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback

        psVenmoController.handleTokenizeResultSuccess(
            context = mockActivity,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            result = PSResult.Success(paymentHandle(action = "NONE"))
        )

        verify(exactly = 0) { psVenmoController.activityResultLauncher.launch(any()) }
        verify { mockPSVenmoTokenizeCallback.onSuccess("paymentHandleToken") }
    }

    @Test
    fun `IF payment handle is PAYABLE THEN Venmo is not opened and token is returned`() {
        val psVenmoController = providePSVenmoNativeController()
        psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback

        psVenmoController.handleTokenizeResultSuccess(
            context = mockActivity,
            profileId = "profile-id",
            amount = 1000,
            appLinkReturnUrl = appLinkReturnUrl,
            customUrlScheme = customUrlScheme,
            result = PSResult.Success(paymentHandle(status = "PAYABLE", action = "REDIRECT"))
        )

        verify(exactly = 0) { psVenmoController.activityResultLauncher.launch(any()) }
        verify { mockPSVenmoTokenizeCallback.onSuccess("paymentHandleToken") }
    }

    private fun collectorReturning(deviceData: String) =
        DeviceDataCollector { _, authorization, clientMetadataId, callback ->
            collectedAuthorization = authorization
            collectedMetadataId = clientMetadataId
            callback(Result.success(deviceData))
        }

    private var collectedMetadataId: String? = null
    private var collectedAuthorization: String? = null

    private fun paymentHandle(
        brainTreeCustomerId: String? = null,
        clientMetadataId: String? = null,
        clientToken: String? = this.clientToken,
        status: String = "PROCESSING",
        action: String = "NONE"
    ) = PaymentHandle(
        merchantRefNum = "merchantRefNum",
        paymentHandleToken = "paymentHandleToken",
        status = status,
        id = orderId,
        gatewayResponse = GatewayResponseSerializable(
            sessionToken = sessionToken,
            clientToken = clientToken,
            clientMetadataId = clientMetadataId,
            brainTreeCustomerId = brainTreeCustomerId
        ),
        action = action
    )
}

