/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.mapper

import com.paysafe.android.hostedfields.data.mapper.toDomain
import com.paysafe.android.tokenization.domain.model.cardadapter.AuthenticationStatus
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthenticationResponseMapperTest {

    @Test
    fun `IF finalize authentication response contains data THEN toDomain RETURNS FinalizeAuthenticationResponse with data`() {
        // Arrange
        val statusInput = "COMPLETED"
        val input =
            com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable(
                status = statusInput
            )

        // Act
        val output = input.toDomain()
        val encoded =
            Json.encodeToString(com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable.serializer(), input)
        val decoded =
            Json.decodeFromString(com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable.serializer(), encoded)

        // Assert
        assertEquals(input, decoded)
        assertEquals(AuthenticationStatus.fromString(statusInput), output.status)
    }

    @Test
    fun `IF finalize authentication response contains null data THEN toDomain RETURNS FinalizeAuthenticationResponse with null data`() {
        // Arrange
        val statusInput = null
        val input =
            com.paysafe.android.hostedfields.data.entity.cardAdapter.FinalizeAuthenticationResponseSerializable(
                status = statusInput
            )

        // Act
        val output = input.toDomain()

        // Assert
        assertEquals(AuthenticationStatus.fromString(statusInput), output.status)
    }

    @Test
    fun `IF authentication response contains data THEN toDomain RETURNS AuthenticationResponse with data`() {
        // Arrange
        val statusInput = "PENDING"
        val sdkChallengePayloadInput = "sdkChallengePayload"
        val input =
            com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable(
                status = statusInput,
                sdkChallengePayload = sdkChallengePayloadInput
            )

        // Act
        val output = input.toDomain()
        val encoded =
            Json.encodeToString(com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable.serializer(), input)
        val decoded =
            Json.decodeFromString(com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable.serializer(), encoded)

        // Assert
        assertEquals(input, decoded)
        assertEquals(sdkChallengePayloadInput, output.sdkChallengePayload)
        assertEquals(AuthenticationStatus.fromString(statusInput), output.status)
    }

    @Test
    fun `IF authentication response contains null data THEN toDomain RETURNS AuthenticationResponse with null data`() {
        // Arrange
        val statusInput = "FAILED"
        val input =
            com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationResponseSerializable(
                status = statusInput
            )

        // Act
        val output = input.toDomain()

        // Assert
        assertNull(output.sdkChallengePayload)
        assertEquals(AuthenticationStatus.fromString(statusInput), output.status)
    }

}