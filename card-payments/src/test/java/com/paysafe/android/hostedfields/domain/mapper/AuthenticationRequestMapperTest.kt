/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.mapper

import com.paysafe.android.hostedfields.data.mapper.toData
import com.paysafe.android.hostedfields.domain.model.cardadapter.AuthenticationRequest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthenticationRequestMapperTest {

    @Test
    fun `IF AuthenticationRequest contains data THEN toData RETURNS AuthenticationRequestSerializable with data`() {
        // Arrange
        val paymentTokenId = "paymentTokenId"
        val deviceFingerprintingIdInput = "deviceFingerprintingId"
        val merchantRefNumInput = "merchantRefNum"
        val processInput = true
        val input = AuthenticationRequest(
            paymentHandleId = paymentTokenId,
            merchantRefNum = merchantRefNumInput,
            process = processInput
        )

        // Act
        val output = input.toData(deviceFingerprintingIdInput)
        val encoded = Json.encodeToString(com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationRequestSerializable.serializer(), output)
        val decoded = Json.decodeFromString(com.paysafe.android.hostedfields.data.entity.cardAdapter.AuthenticationRequestSerializable.serializer(), encoded)

        // Assert
        assertEquals(output, decoded)
        assertEquals(deviceFingerprintingIdInput, output.deviceFingerprintingId)
        assertEquals(merchantRefNumInput, output.merchantRefNum)
        assertEquals(processInput, output.process)
    }

    @Test
    fun `IF AuthenticationRequest contains null data THEN toData RETURNS AuthenticationRequestSerializable with null data`() {
        // Arrange
        val paymentTokenId = "paymentTokenId"
        val deviceFingerprintingIdInput = "deviceFingerprintingId"
        val merchantRefNumInput = "merchantRefNum"
        val input = AuthenticationRequest(
            paymentHandleId = paymentTokenId,
            merchantRefNum = merchantRefNumInput
        )

        // Act
        val output = input.toData(deviceFingerprintingIdInput)

        // Assert
        assertEquals(deviceFingerprintingIdInput, output.deviceFingerprintingId)
        assertEquals(merchantRefNumInput, output.merchantRefNum)
        assertNull(output.process)
    }

}