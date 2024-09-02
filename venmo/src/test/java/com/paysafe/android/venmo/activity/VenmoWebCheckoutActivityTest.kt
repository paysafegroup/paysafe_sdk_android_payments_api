package com.paysafe.android.venmo.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.work.Configuration
import androidx.work.WorkManager
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.VenmoAccountNonce
import com.braintreepayments.api.VenmoClient
import com.braintreepayments.api.VenmoRequest
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.service.PSApiClient
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class VenmoWebCheckoutActivityTest {

    private val application = RuntimeEnvironment.getApplication()

    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockBraintreeClient: BraintreeClient
    private lateinit var mockVenmoClient: VenmoClient
    private lateinit var spyActivity: VenmoWebCheckoutActivity

    private val sessionToken = "SESSION_TOKEN_MOCK"
    private val clientToken = "CLIENT_TOKEN_MOCK"
    private val profileId = "PROFILE_ID_MOCK"
    private val amount = "AMOUNT"
    private val customUrlScheme = "CUSTOM_URL_SCHEME_MOCK"
    private lateinit var venmoAccountNonce: VenmoAccountNonce

    private val intent = Intent().apply {
        putExtra(VenmoConstants.INTENT_EXTRA_SESSION_TOKEN, sessionToken)
        putExtra(VenmoConstants.INTENT_EXTRA_CLIENT_TOKEN, clientToken)
        putExtra(VenmoConstants.INTENT_EXTRA_PROFILE_ID, profileId)
        putExtra(VenmoConstants.INTENT_EXTRA_AMOUNT, amount)
        putExtra(VenmoConstants.INTENT_EXTRA_CUSTOM_URL_SCHEME, customUrlScheme)
    }

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockPSApiClient = mockk(relaxed = true)
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient
        mockBraintreeClient = mockk(relaxed = true)
        mockVenmoClient = mockk(relaxed = true)
        spyActivity = spyk(Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, intent).get()) {
            every { sessionToken } returns this@VenmoWebCheckoutActivityTest.sessionToken
            every { clientToken } returns this@VenmoWebCheckoutActivityTest.clientToken
            every { profileId } returns this@VenmoWebCheckoutActivityTest.profileId
        }
        venmoAccountNonce = mockk {
            every { string } returns "mockedString"
            every { username } returns "mockedUsername"
            every { firstName } returns "mockedFirstName"
            every { lastName } returns "mockedLastName"
            every { phoneNumber } returns "mockedPhoneNumber"
            every { email } returns "mockedEmail"
            every { externalId } returns "mockedExternalId"
        }
    }

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `IF onVenmoFailure THEN finishActivityWithResult is called with RESULT_FAILED`() {
        // Arrange
        val expectedIntent = slot<Intent>()
        every { spyActivity.finishActivityWithResult(any(), capture(expectedIntent)) } just Runs

        // Act
        spyActivity.onVenmoFailure(Exception("error.message"))

        // Verify
        verify {
            spyActivity.finishActivityWithResult(
                VenmoConstants.RESULT_FAILED,
                withArg {
                    assertEquals("error.message", it.getStringExtra("ERROR_MESSAGE"))
                }
            )
        }
    }

    @Test
    fun `IF onVenmoSuccess THEN finishActivityWithResult is called with RESULT_SUCCESS`() {
        // Arrange
        val intentSlot = slot<Intent>()
        every { spyActivity.setResult(any(), capture(intentSlot)) } just Runs
        every { spyActivity.finish() } just Runs
        every { spyActivity.sessionToken } returns sessionToken

        // Act
        spyActivity.onVenmoSuccess(venmoAccountNonce)

        // Verify
        verify { spyActivity.setResult(VenmoConstants.RESULT_SUCCESS, any()) }
        verify { spyActivity.finish() }

        // Verify Intent extras
        val resultIntent = intentSlot.captured
        assertEquals("mockedString", resultIntent.getStringExtra("VENMO_ACCOUNT_NONCE"))
        assertEquals(sessionToken, resultIntent.getStringExtra("JWT_SESSION_TOKEN"))
        assertEquals("mockedUsername", resultIntent.getStringExtra("USER_NAME"))
        assertEquals("mockedFirstName", resultIntent.getStringExtra("FIRST_NAME"))
        assertEquals("mockedLastName", resultIntent.getStringExtra("LAST_NAME"))
        assertEquals("mockedPhoneNumber", resultIntent.getStringExtra("PHONE_NUMBER"))
        assertEquals("mockedEmail", resultIntent.getStringExtra("EMAIL"))
        assertEquals("mockedExternalId", resultIntent.getStringExtra("EXTERNAL_ID"))
    }




    @Test
    fun `IF handleOnCreate without expected data in intent THEN finishActivityWithResult is called with RESULT_FAILED`() {
        // Arrange
        spyActivity = spyk(Robolectric.buildActivity(VenmoWebCheckoutActivity::class.java, null).get())

        // Act
        spyActivity.handleOnCreate()

        // Verify
        verify {
            spyActivity.finishActivityWithResult(VenmoConstants.RESULT_FAILED, intent = null)
        }
    }

    @Test
    fun `IF handleOnCreate THEN finishActivityWithResult is called with RESULT_FAILED`() {
        // Arrange
        every { spyActivity.applicationContext } returns application

        every { spyActivity.intent } returns intent

        // Act
        spyActivity.handleOnCreate()

        // Verify
        Assert.assertEquals(sessionToken, spyActivity.sessionToken)
        Assert.assertEquals(clientToken, spyActivity.clientToken)
        Assert.assertEquals(profileId, spyActivity.profileId)
        Assert.assertEquals(customUrlScheme, spyActivity.customUrlScheme)
    }



    @Test
    fun `IF handleLifecycleObserverOnResume THEN VenmoCheckout is started and the observer is removed`() {
        // Arrange

        // Act
        spyActivity.handleLifecycleObserverOnResume()

        // Verify
        verify {
            spyActivity.removeLifecycleObserver()
        }
    }

    @Test
    fun `IF isAppInstalled and app is installed THEN return true`() {
        // Arrange
        val context = mockk<Context>(relaxed = true)
        val packageManager = mockk<PackageManager>()
        every { context.packageManager } returns packageManager
        every { packageManager.getPackageInfo(VenmoConstants.VENMO_PACKAGE, PackageManager.GET_ACTIVITIES) } returns mockk()

        // Act
        val result = spyActivity.isAppInstalled(context)

        // Assert
        Assert.assertTrue(result)
    }

    @Test
    fun `IF isAppInstalled and app is not installed THEN return false`() {
        // Arrange
        val context = mockk<Context>(relaxed = true)
        val packageManager = mockk<PackageManager>()
        every { context.packageManager } returns packageManager
        every { packageManager.getPackageInfo(VenmoConstants.VENMO_PACKAGE, PackageManager.GET_ACTIVITIES) } throws PackageManager.NameNotFoundException()

        // Act
        val result = spyActivity.isAppInstalled(context)

        // Assert
        Assert.assertFalse(result)
    }

    @Test
    fun `IF launchVenmo THEN braintreeClient and venmoClient are initialized correctly`() {
        // Arrange
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
        WorkManager.initialize(spyActivity, config)

        mockBraintreeClient = spyk(
            BraintreeClient(spyActivity, clientToken)
        )
        mockVenmoClient = spyk(VenmoClient(spyActivity, mockBraintreeClient))

        // Mock methods
        every { mockVenmoClient.isVenmoAppSwitchAvailable(any()) } returns true
        justRun { mockVenmoClient.tokenizeVenmoAccount(any(), any()) }

        // Act
        spyActivity.handleOnCreate()
        val result =  spyActivity.launchVenmo()

        // Verify
        Assert.assertNotNull(result)
    }

    @Test
    fun `IF onCreate THEN handleOnCreate and launchVenmo is called`() {
        // Arrange

        // Mock `launchVenmo and handleOnCreate`
        justRun { spyActivity.handleOnCreate()}
        justRun { spyActivity.launchVenmo()}

        // Act
        spyActivity.onCreate(null)

        // Verify
        verify {
            spyActivity.handleOnCreate()
            spyActivity.launchVenmo()
        }
    }

    @Test
    fun `IF launchVenmo THEN tokenizeVenmoAccount is called`() {
        // Arrange
        val requestSlot = slot<VenmoRequest>()
        every { mockVenmoClient.tokenizeVenmoAccount(any(), capture(requestSlot)) } just Runs

        every { spyActivity.braintreeClient } returns mockBraintreeClient
        every { spyActivity.venmoClient } returns mockVenmoClient
        every { mockVenmoClient.isVenmoAppSwitchAvailable(spyActivity) } returns true

        // Act
        spyActivity.handleOnCreate()
        spyActivity.launchVenmo()

        // Assert
        verify { mockVenmoClient.tokenizeVenmoAccount(spyActivity, any()) }

        // Verify the captured request
        val capturedRequest = requestSlot.captured
        Assert.assertNotNull(capturedRequest)
        Assert.assertTrue(capturedRequest.collectCustomerBillingAddress)
        Assert.assertTrue(capturedRequest.collectCustomerShippingAddress)
        Assert.assertEquals(profileId, capturedRequest.profileId)
        Assert.assertFalse(capturedRequest.shouldVault)
    }
}