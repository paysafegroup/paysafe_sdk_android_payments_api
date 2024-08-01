/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.domain.model

enum class CardNumberSeparator(val id: Int) {
    WHITESPACE(0),
    NONE(1),
    DASH(2),
    SLASH(3);

    companion object {
        operator fun get(value: Int): CardNumberSeparator =
            values().associateBy { it.id }[value] ?: WHITESPACE
    }
}