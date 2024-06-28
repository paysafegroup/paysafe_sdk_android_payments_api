/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.venmo

import android.content.Context
import com.paysafe.android.venmo.domain.model.PSVenmoTokenizeOptions

internal interface PSVenmo {

    suspend fun tokenize(
        context: Context,
        psVenmoTokenizeOptions: PSVenmoTokenizeOptions,
        callback: PSVenmoTokenizeCallback
    )

    fun dispose()

}