/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton

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

@Composable
fun PSGooglePayButton(
    onClick: () -> Unit,
    allowedPaymentMethods: String,
    modifier: Modifier = Modifier,
    theme: PSGooglePayButtonTheme,
    type: PSGooglePayButtonType,
    radius: Dp = 100.dp
) {
    PayButton(
        onClick = onClick,
        type = payButtonType(type),
        radius = radius,
        theme = payButtonTheme(theme),
        allowedPaymentMethods = allowedPaymentMethods,
        modifier = modifier
    )
}