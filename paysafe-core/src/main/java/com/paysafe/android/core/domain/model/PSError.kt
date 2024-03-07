package com.paysafe.android.core.domain.model

internal data class PSError(
    val code: Int,
    val message: String,
    val details: List<String>
)
