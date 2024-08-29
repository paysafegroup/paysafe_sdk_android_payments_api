package com.paysafe.android.hostedfields.data

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationRequestSerializable
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CardAdapterAuthApiTest {

    private val mockPSApiClient = mockk<PSApiClient>()
    private val cardAdapterAuthApi =
        com.paysafe.android.hostedfields.data.api.CardAdapterAuthApi(mockPSApiClient)

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `IF startAuthentication THEN startAuthentication RETURNS Success`() =
        runTest {
            // Arrange
            val deviceFingerprintingId = "deviceFingerprintingId"
            val merchantRefNum = "merchantRefNum"
            val paymentHandleId = "paymentHandleId"
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            coEvery { mockPSApiClient.internalMakeRequest(any()) } returns PSResult.Success()

            // Act
            val result = cardAdapterAuthApi.startAuthentication(
                requestBody = com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationRequestSerializable(
                    deviceFingerprintingId = deviceFingerprintingId,
                    merchantRefNum = merchantRefNum
                ),
                paymentHandleId = paymentHandleId
            )

            // Assert
            assertTrue(result is PSResult.Success)
        }

    @Test
    fun `IF finalizeAuthentication and internalMakeRequest returns Failure THEN finalizeAuthentication RETURNS Failure`() =
        runTest {
            // Arrange
            val paymentHandleId = "paymentHandleId"
            val authenticationId = "authenticationId"
            Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
            val exceptedException = Exception()
            coEvery {
                mockPSApiClient.internalMakeRequest(any())
            } returns PSResult.Failure(exceptedException)

            // Act
            val result = cardAdapterAuthApi.finalizeAuthentication(
                paymentHandleId = paymentHandleId,
                authenticationId = authenticationId
            )

            // Assert
            assertEquals(exceptedException, (result as PSResult.Failure).exception)
        }
}
