package com.paysafe.android.venmo

import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.lifecycleScope
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.tokenization.PSTokenization
import com.paysafe.android.tokenization.PSTokenizationService
import com.paysafe.android.venmo.activity.VenmoConstants
import com.paysafe.android.venmo.exception.genericApiErrorException
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
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
    fun `IF dispose THEN data is cleared`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.dispose()

            // Verify
            verify(exactly = 1) {
                psVenmoController.activityResultLauncher.unregister()
            }
        }

    @Test
    fun `IF startVenmoCheckout THEN intent is launched via activityResultLauncher`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.startVenmoCheckout(
                context = mockActivity,
                orderId = orderId,
                sessionToken = sessionToken,
                clientToken = clientToken,
                profileId = "profile-id",
                amount = 1000,
                customUrlScheme = customUrlScheme
            )

            // Verify
            verify(exactly = 1) {
                psVenmoController.activityResultLauncher.launch(any())
            }
        }

    @Test
    fun `IF handleActivityResult with RESULT_SUCCESS THEN onVenmoSuccess is called`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()
            val resultIntent = Intent().apply {
                putExtra("VENMO_ACCOUNT_NONCE", "venmoAccountNonce.string")
                putExtra("JWT_SESSION_TOKEN", sessionToken)
                putExtra("USER_NAME", "venmoAccountNonce.username")
                putExtra("FIRST_NAME", "venmoAccountNonce.firstName")
                putExtra("LAST_NAME", "venmoAccountNonce.lastName")
                putExtra("PHONE_NUMBER", "venmoAccountNonce.phoneNumber")
                putExtra("EMAIL", "venmoAccountNonce.email")
                putExtra("EXTERNAL_ID", "venmoAccountNonce.externalId")
            }

            // Act
            psVenmoController.handleActivityResult(VenmoConstants.RESULT_SUCCESS, resultIntent)

            // Verify
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleActivityResult with RESULT_FAILED THEN onVenmoFailure is called`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()

            // Act
            psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback
            psVenmoController.handleActivityResult(VenmoConstants.RESULT_FAILED, data = null)

            // Verify
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
        }

    @Test
    fun `IF handleActivityResult with unknown code THEN onVenmoFailure is called`() =
        runTest {
            // Arrange
            val psVenmoController = providePSVenmoNativeController()
            val unknownResultCode = -1

            // Act
            psVenmoController.tokenizeCallback = mockPSVenmoTokenizeCallback
            psVenmoController.handleActivityResult(unknownResultCode, data = null)

            // Verify
            verify {
                mockPSVenmoTokenizeCallback.onFailure(genericApiErrorException(correlationId))
            }
            Assert.assertFalse(psVenmoController.tokenizationAlreadyInProgress)
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
}

