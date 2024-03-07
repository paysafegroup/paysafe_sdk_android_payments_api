/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay

import com.paysafe.android.google_pay.button.PSGooglePayPaymentMethodConfig
import com.paysafe.android.google_pay.domain.model.PSGooglePayTokenizeOptions

interface PSGooglePay {
    fun tokenize(
        googlePayTokenizeOptions: PSGooglePayTokenizeOptions,
        callback: PSGooglePayTokenizeCallback
    )

    fun providePaymentMethodConfig(): PSGooglePayPaymentMethodConfig
}