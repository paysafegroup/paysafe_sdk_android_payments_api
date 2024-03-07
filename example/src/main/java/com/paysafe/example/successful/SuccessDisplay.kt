/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.successful

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SuccessDisplay(
    val accountId: String? = "",
    val merchantReferenceNumber: String? = "",
    val paymentHandleToken: String? = ""
) : Parcelable