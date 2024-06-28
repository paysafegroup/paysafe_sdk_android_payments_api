package com.paysafe.android.venmo


import android.os.Looper.getMainLooper
import androidx.activity.result.ActivityResultCaller
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.venmo.domain.model.PSVenmoConfig
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class PSVenmoContextTest {

    private lateinit var mockFragment: Fragment
    private lateinit var mockActivity: AppCompatActivity
    private lateinit var mockCallback: PSCallback<PSVenmoContext>
    private lateinit var mockPSApiClient: PSApiClient
    private lateinit var mockActivityResultCaller: ActivityResultCaller
    private val validConfig = PSVenmoConfig(currencyCode = "USD" , accountId = "123")

    @Before
    fun setUp() {
        mockkObject(PaysafeSDK)
        justRun { PaysafeSDK.setup(any()) }
        mockFragment = mockk(relaxed = true)
        mockCallback = mockk<PSCallback<PSVenmoContext>>()
        mockActivity = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        mockActivityResultCaller = mockk<ActivityResultCaller>(relaxed = true)
        mockPSApiClient = mockk(relaxed = true)
        every { PaysafeSDK.getPSApiClient() } returns mockPSApiClient

        justRun { mockCallback.onFailure(any()) }
        justRun { mockCallback.onSuccess(any()) }

        mockkObject(PSVenmoController)
        coEvery {
            PSVenmoController.initialize(any(), any(), any(), any())
        } returns PSResult.Failure(Exception("Mocked failure"))
    }

    @Test
    fun `IF initialize with fragment and PaysafeSDK not initialized THEN initialize RETURNS via callback onFailure`() = runTest {
    //Arrange
        val fragment = Fragment()
        mockActivity.supportFragmentManager.beginTransaction().apply {
            add(fragment, "TestFragment")
            commitNow()
        }
        // Act
        PSVenmoContext.initialize(
            fragment,
            validConfig,
            mockCallback
        )

        shadowOf(getMainLooper()).idle()

        // Verify
        verify(exactly = 1) {
            mockCallback.onFailure(any())
        }
    }

    @Test
    fun `IF initialize with activity and PaysafeSDK not initialized THEN initialize RETURNS via callback onFailure`() =
        runTest {
            // Act
            PSVenmoContext.initialize(
                mockActivity,
                validConfig,
                mockCallback
            )

            shadowOf(getMainLooper()).idle()
            // Verify
            verify(exactly = 1) {
                mockCallback.onFailure(any())
            }
        }

    @Test
    fun `IF initialize with fragment and exception occurs THEN initialize RETURNS via callback onFailure`() = runTest {
        // Arrange
        val fragment = Fragment()
        mockActivity.supportFragmentManager.beginTransaction().apply {
            add(fragment, "TestFragment")
            commitNow()
        }
        every { PaysafeSDK.isInitialized() } throws RuntimeException("Initialization error")

        // Act
        PSVenmoContext.initialize(
            fragment,
            validConfig,
            mockCallback
        )

        shadowOf(getMainLooper()).idle()
        // Verify
        verify(exactly = 1) {
            mockCallback.onFailure(any<RuntimeException>())
        }
    }

    @Test
    fun `IF initialize with activity and exception occurs THEN initialize RETURNS via callback onFailure`() = runTest {
        // Arrange
        every { PaysafeSDK.isInitialized() } throws RuntimeException("Initialization error")

        // Act
        PSVenmoContext.initialize(
            mockActivity,
            validConfig,
            mockCallback
        )
        shadowOf(getMainLooper()).idle()
        // Verify
        verify(exactly = 1) {
            mockCallback.onFailure(any<RuntimeException>())
        }
    }

    @Test
    fun `IF initialize and Controller returns Success THEN initialize RETURNS via callback onSuccess`() =
        runTest {
            // Arrange
            every { PaysafeSDK.isInitialized() } returns true
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            mockkObject(PSVenmoController)
            val mockVenmoController = mockk<PSVenmoController>()
            coEvery {
                PSVenmoController.initialize(any(), any(), any(), any())
            } returns PSResult.Success(mockVenmoController)

            // Act
            PSVenmoContext.initialize(
                mockActivity,
                validConfig,
                mockCallback
            )

            // Verify
            verify(exactly = 1) {
                mockCallback.onSuccess(any())
            }
        }

    @Test
    fun `IF dispose THEN controller dispose is called`() =
        runTest {
            // Arrange
            val mockController = mockk<PSVenmoController>()
            justRun { mockController.dispose() }
            val psVenmoContext = PSVenmoContext(mockController)

            // Act
            psVenmoContext.dispose()

            // Verify
            verify(exactly = 1) {
                mockController.dispose()
            }
        }

}