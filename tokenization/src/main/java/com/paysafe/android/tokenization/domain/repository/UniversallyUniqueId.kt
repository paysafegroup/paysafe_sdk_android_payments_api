/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.repository

fun interface UniversallyUniqueId {
    fun generate(): String
}