/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.data

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.service.PSApiClient
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationRequest
import com.paysafe.android.hostedfields.domain.repository.CardAdapterAuthRepository
import com.paysafe.android.hostedfields.exception.GENERIC_API_ERROR
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CardAdapterAuthRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    private val accountIdInput = "2001456398"
    private val paymentHandleIdInput = "904400515151"
    private val deviceFingerprintingId = "deviceFingerprintingId"
    private val merchantRefNumberInput = "1232131231"
    private val authenticationIdInput = "2424000111787878"
    private val processInput = true
    private val statusInput = "COMPLETED"
    private val sdkChallengePayload = "ChallengePayloadAvailable"
    private val mockPSApiClient: PSApiClient = mockk()

    @RelaxedMockK
    private lateinit var cardAdapterAuthApi: com.paysafe.android.hostedfields.data.api.CardAdapterAuthApi

    private lateinit var cardAdapterAuthRepository: CardAdapterAuthRepository

    private val authenticationResponseOutput =
        com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable(
            status = statusInput,
            sdkChallengePayload = sdkChallengePayload
        )
    private val finalizeAuthenticationResponseSerializableOutput =
        com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable(
            status = statusInput
        )

    @Before
    fun setUp() {
        every { mockPSApiClient.getCorrelationId() } returns "correlationId123"
        every { mockPSApiClient.logErrorEvent(any(), any(), any()) } just Runs
        cardAdapterAuthRepository =
            com.paysafe.android.hostedfields.data.repository.CardAdapterAuthRepositoryImpl(
                cardAdapterAuthApi,
                mockPSApiClient
            )
    }

    @After
    fun clear() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `IF start authentication is success THEN start authentication RETURNS Success with a AuthenticationResponse`() =
        runTest {
            // Arrange
            val inputParams = AuthenticationRequest(
                paymentHandleIdInput,
                merchantRefNumberInput,
                processInput
            )
            coEvery {
                cardAdapterAuthApi.startAuthentication(any(), paymentHandleIdInput)
            } returns PSResult.Success(authenticationResponseOutput)

            // Act
            val output =
                cardAdapterAuthRepository.startAuthentication(inputParams, deviceFingerprintingId)

            // Assert
            assertTrue(output is PSResult.Success)
            val successOutput = (output as PSResult.Success).value
            assertEquals(AuthenticationStatus.COMPLETED, successOutput?.status)
            assertEquals(sdkChallengePayload, successOutput?.sdkChallengePayload)
        }

    @Test
    fun `IF start authentication is success, but value response throws Exception THEN startAuthentication RETURNS Failure`() =
        runTest {
            // Arrange
            val inputParams = AuthenticationRequest(
                paymentHandleIdInput,
                merchantRefNumberInput,
                processInput
            )
            val alteredResponse = mockk<com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable> {
                every { status } throws Exception()
                every { sdkChallengePayload } throws Exception()
            }
            coEvery {
                cardAdapterAuthApi.startAuthentication(any(), paymentHandleIdInput)
            } returns PSResult.Success(alteredResponse)

            // Act
            val output =
                cardAdapterAuthRepository.startAuthentication(inputParams, deviceFingerprintingId)

            // Assert
            assertTrue(output is PSResult.Failure)
            assertTrue((output as PSResult.Failure).exception is PaysafeException)
            val paysafeException = output.exception as PaysafeException
            assertEquals(GENERIC_API_ERROR, paysafeException.code)
        }

    @Test
    fun `IF start authentication fails THEN startAuthentication RETURNS Failure`() = runTest {
        // Arrange
        val inputParams = AuthenticationRequest(
            paymentHandleIdInput,
            merchantRefNumberInput,
            processInput
        )

        // Act
        val output = cardAdapterAuthRepository.startAuthentication(inputParams, accountIdInput)

        // Assert
        assertTrue(output is PSResult.Failure)
        val failureOutput = (output as PSResult.Failure)
        assertEquals(failureOutput.reason, "")
        assertNotNull(failureOutput.exception)
    }

    @Test
    fun `IF finalize authentication is success THEN finalizeAuthentication RETURNS Success with FinalizeAuthenticationResponse`() =
        runTest {
            // Arrange
            coEvery {
                cardAdapterAuthApi.finalizeAuthentication(
                    paymentHandleIdInput,
                    authenticationIdInput
                )
            } returns PSResult.Success(finalizeAuthenticationResponseSerializableOutput)

            // Act
            val output = cardAdapterAuthRepository.finalizeAuthentication(
                paymentHandleIdInput,
                authenticationIdInput
            )

            // Assert
            assertTrue(output is PSResult.Success)
            val successOutput = (output as PSResult.Success).value
            assertEquals(AuthenticationStatus.COMPLETED, successOutput?.status)
        }

    @Test
    fun `IF finalize authentication is success, but value response throws Exception THEN finalizeAuthentication RETURNS Failure`() =
        runTest {
            // Arrange
            val alteredResponse = mockk<com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable> {
                every { status } throws Exception()
            }
            coEvery {
                cardAdapterAuthApi.finalizeAuthentication(
                    paymentHandleIdInput,
                    authenticationIdInput
                )
            } returns PSResult.Success(alteredResponse)

            // Act
            val output = cardAdapterAuthRepository.finalizeAuthentication(
                paymentHandleIdInput,
                authenticationIdInput
            )

            // Assert
            assertTrue(output is PSResult.Failure)
            assertTrue((output as PSResult.Failure).exception is PaysafeException)
            val paysafeException = output.exception as PaysafeException
            assertEquals(GENERIC_API_ERROR, paysafeException.code)
        }

    @Test
    fun `IF finalize authentication fails THEN finalizeAuthentication RETURNS Failure`() =
        runTest {
            // Arrange

            // Act
            val output = cardAdapterAuthRepository.finalizeAuthentication(
                paymentHandleIdInput,
                authenticationIdInput
            )

            // Assert
            assertTrue(output is PSResult.Failure)
            val failureOutput = (output as PSResult.Failure)
            assertEquals(failureOutput.reason, "")
            assertNotNull(failureOutput.exception)
        }

}