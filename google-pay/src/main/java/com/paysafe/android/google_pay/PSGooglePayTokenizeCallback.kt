/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay

import com.paysafe.android.core.domain.exception.PaysafeException

interface PSGooglePayTokenizeCallback {

    fun onSuccess(paymentHandleToken: String)

    fun onFailure(paysafeException: PaysafeException)

    fun onCancelled(paysafeException: PaysafeException)
}