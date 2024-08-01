/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.cardadapter

enum class AuthenticationStatus(val value: String) {
    COMPLETED("COMPLETED"),
    PENDING("PENDING"),
    FAILED("FAILED"),
    UNKNOWN("");

    companion object {
        fun fromString(string: String?): AuthenticationStatus = when (string) {
            "COMPLETED" -> COMPLETED
            "PENDING" -> PENDING
            "FAILED" -> FAILED
            else -> UNKNOWN
        }
    }
}