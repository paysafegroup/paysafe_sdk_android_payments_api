/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.paymentHandle

import com.paysafe.android.hostedfields.domain.model.toThreeDSRenderType
import com.paysafe.android.threedsecure.domain.model.ThreeDSRenderType
import org.junit.Assert.assertEquals
import org.junit.Test

class RenderTypeTest {

    @Test
    fun `IF RenderType NATIVE THEN toThreeDSRenderType RETURNS correct ThreeDSRenderType`() {
        // Arrange
        val renderType = com.paysafe.android.hostedfields.domain.model.RenderType.NATIVE

        // Act
        val threeDSRenderType = renderType.toThreeDSRenderType()

        // Assert
        assertEquals(ThreeDSRenderType.NATIVE, threeDSRenderType)
    }

    @Test
    fun `IF RenderType HTML THEN toThreeDSRenderType RETURNS correct ThreeDSRenderType`() {
        // Arrange
        val renderType = com.paysafe.android.hostedfields.domain.model.RenderType.HTML

        // Act
        val threeDSRenderType = renderType.toThreeDSRenderType()

        // Assert
        assertEquals(ThreeDSRenderType.HTML, threeDSRenderType)
    }

    @Test
    fun `IF RenderType BOTH THEN toThreeDSRenderType RETURNS correct ThreeDSRenderType`() {
        // Arrange
        val renderType = com.paysafe.android.hostedfields.domain.model.RenderType.BOTH

        // Act
        val threeDSRenderType = renderType.toThreeDSRenderType()

        // Assert
        assertEquals(ThreeDSRenderType.BOTH, threeDSRenderType)
    }

}