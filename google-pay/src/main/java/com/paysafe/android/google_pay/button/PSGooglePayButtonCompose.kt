/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.button

import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType

internal fun payButtonType(
    googlePayButtonType: PSGooglePayButtonType
) = when (googlePayButtonType) {
    PSGooglePayButtonType.BUY -> ButtonType.Buy
    PSGooglePayButtonType.DONATE -> ButtonType.Donate
}

internal fun payButtonTheme(
    googlePayButtonTheme: PSGooglePayButtonTheme
) = when (googlePayButtonTheme) {
    PSGooglePayButtonTheme.LIGHT -> ButtonTheme.Light
    PSGooglePayButtonTheme.DARK -> ButtonTheme.Dark
}