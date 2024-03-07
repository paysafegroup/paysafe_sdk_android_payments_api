/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.util

fun isCurrencyCodeValid(currencyCodeInput: String) = currencyCodeInput.matches(Regex("^[A-Z]{3}$"))

fun String.isAllDigits(): Boolean = all { it.isDigit() }

fun String.isNotAllDigits(): Boolean = !isAllDigits()

fun isAmountNotValid(amount: Int?) =
    amount == null || amount <= 0 || amount.toString().length > 11

fun <T> List<T?>?.isNotNullOrEmpty() = this?.any { it != null } ?: false

fun String?.isNotNullOrEmpty() = !this.isNullOrEmpty()

fun String.tryParseInt() = this.runCatching { this.toInt() }.getOrNull()
