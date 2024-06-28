/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.venmo

import com.paysafe.android.core.domain.exception.PaysafeException

interface PSVenmoTokenizeCallback {
    /**
    Called when Venmo tokenization is successful.
    @param paymentHandle The payment handle representing the tokenized data.
     */
    fun onSuccess(paymentHandleToken: String)

    /**
    Called when an error occurs during Venmo tokenization.
    @param exception The exception indicating the error encountered during tokenization.
     */
    fun onFailure(exception: Exception)

    /**
    Called when Venmo tokenization is cancelled.
    @param paysafeException The Paysafe exception indicating the cancellation reason.
     */
    fun onCancelled(paysafeException: PaysafeException)

}
