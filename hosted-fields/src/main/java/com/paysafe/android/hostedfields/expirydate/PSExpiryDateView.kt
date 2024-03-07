/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.expirydate

import android.content.Context
import androidx.lifecycle.LiveData
import com.paysafe.android.hostedfields.PSCardFormController

/**
 * There are two similar view components to capture expiry date. This interface is to unify month
 * and year data for [PSCardFormController].
 */
interface PSExpiryDateView {
    /** Expiration date month. */
    val monthData: String

    /** Expiration date year. */
    val yearData: String

    /** Live data to store if expiration date is valid. */
    val isValidLiveData: LiveData<Boolean>

    val viewContext: Context

    /** Placeholder string value. */
    val placeholderString: String

    /** Resets the field data. */
    fun reset()
}