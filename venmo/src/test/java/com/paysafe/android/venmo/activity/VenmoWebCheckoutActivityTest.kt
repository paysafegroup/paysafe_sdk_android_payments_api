package com.paysafe.android.venmo.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.braintreepayments.api.venmo.VenmoAccountNonce
import com.braintreepayments.api.venmo.VenmoClient
import com.braintreepayments.api.venmo.VenmoLauncher
import com.braintreepayments.api.venmo.VenmoPaymentAuthRequest
import com.braintreepayments.api.venmo.VenmoPaymentAuthRequestCallback
import com.braintreepayments.api.venmo.VenmoPaymentAuthResult
import com.braintreepayments.api.venmo.VenmoPaymentMethodUsage
import com.braintreepayments.api.venmo.VenmoPendingRequest
import com.braintreepayments.api.venmo.VenmoRequest
import com.braintreepayments.api.venmo.VenmoResult
import com.braintreepayments.api.venmo.VenmoTokenizeCallback
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.venmo.VenmoRequestStorageManage
import com.paysafe.android.venmo.domain.model.BraintreeDetailsConfiguration
import com.paysafe.android.venmo.domain.model.VenmoWebCheckoutConfiguration
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.Runs
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class VenmoWebCheckoutActivityTest {

    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockVenmoLauncher: VenmoLauncher
    private lateinit var mockVenmoClient: VenmoClient
    private lateinit var spyActivity: VenmoWebCheckoutActivity

    private val sessionToken = "SESSION_TOKEN_MOCK"
    private val clientToken = "CLIENT_TOKEN_MOCK"
    private val profileId = "PROFILE_ID_MOCK"
    private val amount = "AMOUNT"
    private val customUrlScheme = "com.paysafe.example.braintree"
    private val appLinkReturnUrl = "https://merchant.example.com"
    private val pendingRequest = "pending-request"
    private val storage = VenmoRequestStorageManage()

    private val checkoutConfiguration = buildCheckoutConfiguration()

    private val checkoutIntent = intentFor(checkoutConfiguration)

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockPSApiClient = mockk(relaxed = true)
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        mockVenmoLauncher = mockk(relaxed = true)
        mockVenmoClient = mockk(relaxed = true)
        spyActivity = spyk(
            Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, checkoutIntent).get()
        )
        storage.clear(spyActivity)
        every { spyActivity.finishActivityWithResult(any(), any()) } just Runs
    }

    @After
    fun clear() {
        storage.clear(spyActivity)
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `IF handleOnCreate has no configuration THEN it throws`() {
        every { spyActivity.intent } returns Intent()

        val error = assertThrows(IllegalStateException::class.java) {
            spyActivity.handleOnCreate(null)
        }

        assertEquals("Missing required configuration", error.message)
    }

    @Test
    fun `IF handleOnCreate THEN configuration is initialized from the intent`() {
        every { spyActivity.intent } returns checkoutIntent

        spyActivity.handleOnCreate(null)

        assertEquals(checkoutConfiguration, spyActivity.configuration)
    }

    @Test
    fun `IF handleOnCreate has a nullable profile and return scheme THEN those values are kept`() {
        val configuration = buildCheckoutConfiguration(profileId = null, customUrlScheme = null)
        every { spyActivity.intent } returns intentFor(configuration)

        spyActivity.handleOnCreate(null)

        assertNull(spyActivity.configuration.profileId)
        assertNull(spyActivity.configuration.customUrlScheme)
        assertEquals(sessionToken, spyActivity.configuration.sessionToken)
        verify(exactly = 0) { spyActivity.finishActivityWithResult(any(), any()) }
    }

    @Test
    fun `IF handleOnCreate has saved configuration THEN it is used instead of the intent`() {
        val savedConfiguration = buildCheckoutConfiguration(profileId = "saved-profile")
        every { spyActivity.intent } returns checkoutIntent

        spyActivity.handleOnCreate(
            bundleOf(VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA to savedConfiguration)
        )

        assertEquals(savedConfiguration, spyActivity.configuration)
    }

    @Test
    fun `IF saved state has no configuration THEN the intent configuration is used`() {
        every { spyActivity.intent } returns checkoutIntent

        spyActivity.handleOnCreate(Bundle())

        assertEquals(checkoutConfiguration, spyActivity.configuration)
    }

    @Test
    fun `IF onSaveInstanceState THEN the checkout configuration is stored`() {
        val controller = Robolectric.buildActivity(
            VenmoWebCheckoutActivity::class.java,
            checkoutIntent
        ).create()
        val outState = Bundle()

        controller.saveInstanceState(outState)

        assertEquals(
            checkoutConfiguration,
            BundleCompat.getParcelable(
                outState,
                VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA,
                VenmoWebCheckoutConfiguration::class.java
            )
        )
    }

    @Test
    fun `IF handleLifecycleObserverOnResume THEN the observer is removed`() {
        // Act
        spyActivity.handleLifecycleObserverOnResume()

        // Verify
        verify { spyActivity.removeLifecycleObserver() }
    }

    @Test
    fun `IF removeLifecycleObserver THEN observer is removed from lifecycle`() {
        // Arrange
        every { spyActivity.intent } returns checkoutIntent
        spyActivity.handleOnCreate(null)

        // Act
        spyActivity.removeLifecycleObserver()

        // Verify - calling again should be safe and not finish the activity
        spyActivity.removeLifecycleObserver()
        verify(exactly = 0) {
            spyActivity.finishActivityWithResult(any(), any())
        }
    }

    @Test
    fun `IF launchVenmo before clients initialized THEN returns without starting auth`() {
        // Arrange
        justRun { spyActivity.startPaymentAuth() }

        // Act
        spyActivity.launchVenmo(checkoutIntent)

        // Verify
        verify(exactly = 0) { spyActivity.startPaymentAuth() }
    }

    @Test
    fun `IF launchVenmo and no pending request THEN startPaymentAuth is called`() {
        // Arrange
        justRun { spyActivity.startPaymentAuth() }
        prepareVenmoClients()

        // Act
        spyActivity.launchVenmo(checkoutIntent)

        // Verify
        verify { spyActivity.startPaymentAuth() }
    }

    @Test
    fun `IF launchVenmo and return is NoResult THEN finish with RESULT_CANCELED and clear storage`() {
        // Arrange
        storage.store(spyActivity, pendingRequest)
        prepareVenmoClients()
        every {
            mockVenmoLauncher.handleReturnToApp(any(), any())
        } returns VenmoPaymentAuthResult.NoResult

        // Act
        spyActivity.launchVenmo(checkoutIntent)

        // Verify
        verify {
            spyActivity.finishActivityWithResult(VenmoConstants.RESULT_CANCELED, null)
        }
        assertNull(storage.fetch(spyActivity))
    }

    @Test
    fun `IF launchVenmo and return is Failure THEN finish with RESULT_FAILED and clear storage`() {
        // Arrange
        val returnIntent = Intent()
        val error = Exception("venmo error")
        val failure = mockk<VenmoPaymentAuthResult.Failure>()
        every { failure.error } returns error
        storage.store(spyActivity, pendingRequest)
        prepareVenmoClients()
        every { mockVenmoLauncher.handleReturnToApp(any(), returnIntent) } returns failure

        // Act
        spyActivity.launchVenmo(returnIntent)

        // Verify
        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_FAILED,
                withArg { assertEquals("venmo error", it.getStringExtra("ERROR_MESSAGE")) }
            )
        }
        assertNull(storage.fetch(spyActivity))
    }

    @Test
    fun `IF launchVenmo and return is Success THEN tokenize and finish with the nonce result`() {
        // Arrange
        val authSuccess = mockk<VenmoPaymentAuthResult.Success>()
        val tokenizeCallback = slot<VenmoTokenizeCallback>()
        val nonce = mockk<VenmoAccountNonce> {
            every { string } returns "nonce"
            every { username } returns "venmo-user"
            every { firstName } returns "Ada"
            every { lastName } returns "Lovelace"
            every { phoneNumber } returns "555"
            every { email } returns "ada@example.com"
            every { externalId } returns "external-id"
        }
        storage.store(spyActivity, pendingRequest)
        prepareVenmoClients()
        spyActivity.handleOnCreate(null)
        every { mockVenmoLauncher.handleReturnToApp(any(), any()) } returns authSuccess
        every { mockVenmoClient.tokenize(authSuccess, capture(tokenizeCallback)) } just Runs

        // Act
        spyActivity.launchVenmo(checkoutIntent)
        tokenizeCallback.captured.onVenmoResult(VenmoResult.Success(nonce))

        // Verify
        verify {
            mockVenmoClient.tokenize(authSuccess, any())
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_SUCCESS,
                withArg {
                    assertEquals(
                        BraintreeDetailsConfiguration(
                            account = "nonce",
                            userName = "venmo-user",
                            firstName = "Ada",
                            lastName = "Lovelace",
                            phoneNumber = "555",
                            email = "ada@example.com",
                            externalId = "external-id",
                            sessionToken = sessionToken,
                            deviceDataCorrelationId = null
                        ),
                        it.braintreeDetails()
                    )
                }
            )
        }
        assertNull(storage.fetch(spyActivity))
    }

    @Test
    fun `IF tokenize returns Cancel THEN finish with RESULT_CANCELED`() {
        // Arrange
        val authSuccess = mockk<VenmoPaymentAuthResult.Success>()
        val tokenizeCallback = slot<VenmoTokenizeCallback>()
        storage.store(spyActivity, pendingRequest)
        prepareVenmoClients()
        every { mockVenmoLauncher.handleReturnToApp(any(), any()) } returns authSuccess
        every { mockVenmoClient.tokenize(authSuccess, capture(tokenizeCallback)) } just Runs

        // Act
        spyActivity.launchVenmo(checkoutIntent)
        tokenizeCallback.captured.onVenmoResult(VenmoResult.Cancel)

        // Verify
        verify {
            spyActivity.finishActivityWithResult(VenmoConstants.RESULT_CANCELED, null)
        }
    }

    @Test
    fun `IF tokenize returns Failure THEN finish with RESULT_FAILED`() {
        // Arrange
        val authSuccess = mockk<VenmoPaymentAuthResult.Success>()
        val tokenizeCallback = slot<VenmoTokenizeCallback>()
        storage.store(spyActivity, pendingRequest)
        prepareVenmoClients()
        every { mockVenmoLauncher.handleReturnToApp(any(), any()) } returns authSuccess
        every { mockVenmoClient.tokenize(authSuccess, capture(tokenizeCallback)) } just Runs

        // Act
        spyActivity.launchVenmo(checkoutIntent)
        tokenizeCallback.captured.onVenmoResult(VenmoResult.Failure(Exception("tokenize failed")))

        // Verify
        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_FAILED,
                withArg { assertEquals("tokenize failed", it.getStringExtra("ERROR_MESSAGE")) }
            )
        }
    }

    @Test
    fun `IF startPaymentAuth receives ReadyToLaunch Started THEN pending request is stored`() {
        // Arrange
        val requestSlot = slot<VenmoRequest>()
        val callbackSlot = slot<VenmoPaymentAuthRequestCallback>()
        val readyToLaunch = mockk<VenmoPaymentAuthRequest.ReadyToLaunch>()
        prepareVenmoClients()
        spyActivity.handleOnCreate(null)
        every {
            mockVenmoClient.createPaymentAuthRequest(any(), capture(requestSlot), capture(callbackSlot))
        } answers {
            callbackSlot.captured.onVenmoPaymentAuthRequest(readyToLaunch)
        }
        every {
            mockVenmoLauncher.launch(spyActivity, readyToLaunch)
        } returns VenmoPendingRequest.Started(pendingRequest)

        // Act
        spyActivity.startPaymentAuth()

        // Verify
        val capturedRequest = requestSlot.captured
        assertEquals(VenmoPaymentMethodUsage.MULTI_USE, capturedRequest.paymentMethodUsage)
        assertEquals(true, capturedRequest.collectCustomerBillingAddress)
        assertEquals(true, capturedRequest.collectCustomerShippingAddress)
        assertEquals(profileId, capturedRequest.profileId)
        assertEquals(false, capturedRequest.shouldVault)
        assertEquals(amount, capturedRequest.totalAmount)
        assertNull(capturedRequest.riskCorrelationId)
        assertEquals(pendingRequest, storage.fetch(spyActivity))
    }

    @Test
    fun `IF startPaymentAuth receives ReadyToLaunch Failure THEN finish with RESULT_FAILED`() {
        // Arrange
        val callbackSlot = slot<VenmoPaymentAuthRequestCallback>()
        val readyToLaunch = mockk<VenmoPaymentAuthRequest.ReadyToLaunch>()
        prepareVenmoClients()
        spyActivity.handleOnCreate(null)
        every {
            mockVenmoClient.createPaymentAuthRequest(any(), any(), capture(callbackSlot))
        } answers {
            callbackSlot.captured.onVenmoPaymentAuthRequest(readyToLaunch)
        }
        every {
            mockVenmoLauncher.launch(spyActivity, readyToLaunch)
        } returns VenmoPendingRequest.Failure(Exception("launch failed"))

        // Act
        spyActivity.startPaymentAuth()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_FAILED,
                withArg { assertEquals("launch failed", it.getStringExtra("ERROR_MESSAGE")) }
            )
        }
    }

    @Test
    fun `IF startPaymentAuth receives Failure THEN finish with RESULT_FAILED`() {
        // Arrange
        val callbackSlot = slot<VenmoPaymentAuthRequestCallback>()
        val requestFailure = mockk<VenmoPaymentAuthRequest.Failure>()
        every { requestFailure.error } returns Exception("auth failed")
        prepareVenmoClients()
        spyActivity.handleOnCreate(null)
        every {
            mockVenmoClient.createPaymentAuthRequest(any(), any(), capture(callbackSlot))
        } answers {
            callbackSlot.captured.onVenmoPaymentAuthRequest(requestFailure)
        }

        // Act
        spyActivity.startPaymentAuth()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_FAILED,
                withArg { assertEquals("auth failed", it.getStringExtra("ERROR_MESSAGE")) }
            )
        }
    }

    @Test
    fun `IF onCreate restores an invalid pending request THEN storage is cleared`() {
        // Arrange
        storage.store(spyActivity, "not-a-pending-request")

        // Act
        Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, checkoutIntent)
            .create()
            .get()

        // Verify
        assertNull(storage.fetch(spyActivity))
    }

    @Test
    fun `IF onCreate restores a valid pending request THEN restorePendingRequest is called and storage kept`() {
        // Arrange
        mockkConstructor(VenmoLauncher::class)
        every { anyConstructed<VenmoLauncher>().restorePendingRequest(pendingRequest) } just Runs
        storage.store(spyActivity, pendingRequest)

        // Act
        Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, checkoutIntent)
            .create()
            .get()

        // Verify
        verify { anyConstructed<VenmoLauncher>().restorePendingRequest(pendingRequest) }
        assertEquals(pendingRequest, storage.fetch(spyActivity))
    }

    @Test
    fun `IF onCreate with no pending request THEN restorePendingRequest is not called`() {
        // Arrange
        mockkConstructor(VenmoLauncher::class)
        every { anyConstructed<VenmoLauncher>().restorePendingRequest(any()) } just Runs

        // Act
        Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, checkoutIntent)
            .create()
            .get()

        // Verify
        verify(exactly = 0) { anyConstructed<VenmoLauncher>().restorePendingRequest(any()) }
    }

    @Test
    fun `IF onCreate and venmoLauncher already initialized THEN existing launcher is reused`() {
        // Arrange
        mockkConstructor(VenmoLauncher::class)
        every { anyConstructed<VenmoLauncher>().restorePendingRequest(any()) } just Runs
        val controller = Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, checkoutIntent)
        val activity = controller.get()
        activity.venmoLauncher = mockVenmoLauncher
        storage.store(activity, pendingRequest)
        every { mockVenmoLauncher.restorePendingRequest(pendingRequest) } just Runs

        // Act
        controller.create()

        // Verify
        assertSame(mockVenmoLauncher, activity.venmoLauncher)
        verify { mockVenmoLauncher.restorePendingRequest(pendingRequest) }
        verify(exactly = 0) { anyConstructed<VenmoLauncher>().restorePendingRequest(any()) }
    }

    @Test
    fun `IF handleOnResume THEN launchVenmo is called with intent`() {
        // Arrange
        prepareVenmoClients()
        justRun { spyActivity.launchVenmo(any()) }
        every { spyActivity.intent } returns checkoutIntent

        // Act
        spyActivity.handleOnResume()

        // Verify
        verify { spyActivity.launchVenmo(checkoutIntent) }
    }

    @Test
    fun `IF handleOnNewIntent THEN intent is updated and launchVenmo is called`() {
        // Arrange
        val newIntent = Intent(checkoutIntent).putExtra("RETURN", "from-venmo")
        prepareVenmoClients()
        justRun { spyActivity.launchVenmo(any()) }
        justRun { spyActivity.setIntent(any()) }

        // Act
        spyActivity.handleOnNewIntent(newIntent)

        // Verify
        verify {
            spyActivity.setIntent(newIntent)
            spyActivity.launchVenmo(newIntent)
        }
    }

    @Test
    fun `IF back pressed THEN storage is cleared and finish with RESULT_FAILED`() {
        // Arrange
        storage.store(spyActivity, pendingRequest)
        val callback = spyActivity.provideOnBackPressedCallback()

        // Act
        callback.handleOnBackPressed()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(VenmoConstants.RESULT_FAILED, null)
        }
        assertNull(storage.fetch(spyActivity))
    }

    @Test
    fun `IF finishActivityWithResult THEN result is set and activity finishes`() {
        // Arrange
        val activity = Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, checkoutIntent)
            .create()
            .get()
        val resultIntent = Intent().putExtra("ERROR_MESSAGE", "boom")

        // Act
        activity.finishActivityWithResult(VenmoConstants.RESULT_FAILED, resultIntent)

        // Verify
        val shadow = shadowOf(activity)
        assertTrue(activity.isFinishing)
        assertEquals(VenmoConstants.RESULT_FAILED, shadow.resultCode)
        assertEquals("boom", shadow.resultIntent?.getStringExtra("ERROR_MESSAGE"))
    }

    @Test
    fun `IF handleOnCreate receives device data THEN the configuration value is kept`() {
        val deviceData = """{"correlation_id":"corr-1"}"""
        storeDeviceData("""{"correlation_id":"stored"}""")
        every { spyActivity.intent } returns intentFor(
            buildCheckoutConfiguration(deviceDataCorrelationId = deviceData)
        )

        spyActivity.handleOnCreate(null)

        assertEquals(deviceData, spyActivity.configuration.deviceDataCorrelationId)
    }

    @Test
    fun `IF handleOnCreate has no device data THEN stored device data is ignored`() {
        storeDeviceData("""{"correlation_id":"stored"}""")

        spyActivity.handleOnCreate(null)

        assertNull(spyActivity.configuration.deviceDataCorrelationId)
    }

    @Test
    fun `IF handleOnCreate receives blank device data THEN the blank value is kept`() {
        every { spyActivity.intent } returns intentFor(
            buildCheckoutConfiguration(deviceDataCorrelationId = " ")
        )

        spyActivity.handleOnCreate(null)

        assertEquals(" ", spyActivity.configuration.deviceDataCorrelationId)
    }

    @Test
    fun `IF venmo succeeds with device data from the checkout configuration THEN that device data is returned`() {
        val deviceData = """{"correlation_id":"corr-1"}"""
        every { spyActivity.intent } returns intentFor(
            buildCheckoutConfiguration(deviceDataCorrelationId = deviceData)
        )
        storeDeviceData("""{"correlation_id":"stored"}""")

        completeSuccessfulReturn()

        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_SUCCESS,
                withArg {
                    assertEquals(deviceData, it.braintreeDetails()?.deviceDataCorrelationId)
                    assertEquals(sessionToken, it.braintreeDetails()?.sessionToken)
                    assertEquals("nonce", it.braintreeDetails()?.account)
                }
            )
        }
    }

    @Test
    fun `IF venmo succeeds with blank device data THEN the blank value is returned`() {
        every { spyActivity.intent } returns intentFor(
            buildCheckoutConfiguration(deviceDataCorrelationId = " ")
        )

        completeSuccessfulReturn()

        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_SUCCESS,
                withArg { assertEquals(" ", it.braintreeDetails()?.deviceDataCorrelationId) }
            )
        }
    }

    @Test
    fun `IF venmo succeeds and only stored device data exists THEN the result omits it`() {
        storeDeviceData("""{"correlation_id":"stored"}""")

        completeSuccessfulReturn()

        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_SUCCESS,
                withArg { assertNull(it.braintreeDetails()?.deviceDataCorrelationId) }
            )
        }
    }

    @Test
    fun `IF Open creates an intent THEN the checkout configuration is attached`() {
        val configuration = buildCheckoutConfiguration(deviceDataCorrelationId = "corr-1")

        val intent = VenmoWebCheckoutActivity.Open().createIntent(spyActivity, configuration)

        assertEquals(
            VenmoWebCheckoutActivity::class.java.name,
            intent.component?.className
        )
        assertEquals(configuration, intent.checkoutConfiguration())
    }

    @Test
    fun `IF Open parses a Venmo result THEN the parser result is returned`() {
        val configuration = braintreeDetails(deviceDataCorrelationId = "corr-1")
        val intent = Intent().putExtra(VenmoConstants.VENMO_DATA_EXTRA, configuration)

        val parsed = VenmoWebCheckoutActivity.Open().parseResult(
            VenmoConstants.RESULT_SUCCESS,
            intent
        )

        assertEquals(VenmoWebCheckoutActivityResult.Success(configuration), parsed)
    }

    private fun prepareVenmoClients() {
        spyActivity.venmoLauncher = mockVenmoLauncher
        spyActivity.venmoClient = mockVenmoClient
    }

    private fun completeSuccessfulReturn() {
        val authSuccess = mockk<VenmoPaymentAuthResult.Success>()
        val tokenizeCallback = slot<VenmoTokenizeCallback>()
        val nonce = mockk<VenmoAccountNonce>(relaxed = true) {
            every { string } returns "nonce"
        }
        storage.store(spyActivity, pendingRequest)
        prepareVenmoClients()
        spyActivity.handleOnCreate(null)
        every { mockVenmoLauncher.handleReturnToApp(any(), any()) } returns authSuccess
        every { mockVenmoClient.tokenize(authSuccess, capture(tokenizeCallback)) } just Runs
        spyActivity.launchVenmo(checkoutIntent)
        tokenizeCallback.captured.onVenmoResult(VenmoResult.Success(nonce))
    }

    private fun storeDeviceData(deviceData: String) {
        spyActivity.getSharedPreferences("venmo_pending_request_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("venmo_device_data", deviceData)
            .commit()
    }

    private fun buildCheckoutConfiguration(
        profileId: String? = this.profileId,
        customUrlScheme: String? = this.customUrlScheme,
        deviceDataCorrelationId: String? = null
    ) = VenmoWebCheckoutConfiguration(
        orderId = "order-id",
        sessionToken = sessionToken,
        clientToken = clientToken,
        profileId = profileId,
        amount = amount,
        appLinkReturnUrl = appLinkReturnUrl,
        customUrlScheme = customUrlScheme,
        clientMetadataId = "meta-123",
        deviceDataCorrelationId = deviceDataCorrelationId
    )

    private fun intentFor(configuration: VenmoWebCheckoutConfiguration) = Intent().apply {
        putExtra(VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA, configuration)
    }

    private fun braintreeDetails(deviceDataCorrelationId: String?) = BraintreeDetailsConfiguration(
        account = "nonce",
        userName = "venmo-user",
        firstName = "Ada",
        lastName = "Lovelace",
        phoneNumber = "555",
        email = "ada@example.com",
        externalId = "external-id",
        sessionToken = sessionToken,
        deviceDataCorrelationId = deviceDataCorrelationId
    )

    private fun Intent.checkoutConfiguration(): VenmoWebCheckoutConfiguration? =
        IntentCompat.getParcelableExtra(
            this,
            VenmoConstants.VENMO_CHECKOUT_CONFIGURATION_EXTRA,
            VenmoWebCheckoutConfiguration::class.java
        )

    private fun Intent.braintreeDetails(): BraintreeDetailsConfiguration? =
        IntentCompat.getParcelableExtra(
            this,
            VenmoConstants.VENMO_DATA_EXTRA,
            BraintreeDetailsConfiguration::class.java
        )
}
