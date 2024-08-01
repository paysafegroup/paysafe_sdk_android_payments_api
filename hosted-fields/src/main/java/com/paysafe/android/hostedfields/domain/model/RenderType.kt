/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

import com.paysafe.android.threedsecure.domain.model.ThreeDSRenderType

/**
 * Render type to display specific payment challenges
 */
enum class RenderType {
    NATIVE,
    HTML,
    BOTH
}

internal fun RenderType.toThreeDSRenderType() = when (this) {
    RenderType.NATIVE -> ThreeDSRenderType.NATIVE
    RenderType.HTML -> ThreeDSRenderType.HTML
    RenderType.BOTH -> ThreeDSRenderType.BOTH
}