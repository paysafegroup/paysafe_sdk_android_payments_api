/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import android.content.Context
import com.paysafe.android.paypal.domain.model.PSPayPalTokenizeOptions

internal interface PSPayPal {

    suspend fun tokenize(
        context: Context,
        payPalTokenizeOptions: PSPayPalTokenizeOptions,
        callback: PSPayPalTokenizeCallback
    )

    fun dispose()

}