/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal

import com.paysafe.android.core.domain.exception.PaysafeException

interface PSPayPalTokenizeCallback {

    fun onSuccess(paymentHandleToken: String)

    fun onFailure(exception: Exception)

    fun onCancelled(paysafeException: PaysafeException)

}
