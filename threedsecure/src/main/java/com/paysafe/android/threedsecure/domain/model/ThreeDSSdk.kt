/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure.domain.model

internal data class ThreeDSSdk(
    val type: ThreeDSSdkType? = null,
    val version: String? = null
)

internal enum class ThreeDSSdkType(val value: String) {
    ANDROID("ANDROID"),
    IOS("IOS"),
    BROWSER("BROWSER");

    companion object {
        fun fromType(type: String?) = when (type) {
            "ANDROID" -> ANDROID
            "IOS" -> IOS
            "BROWSER" -> BROWSER
            else -> null
        }
    }
}