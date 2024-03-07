/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle

import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.ChangedRange
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.CreatedRange
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.PasswordChangeRange
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.PaymentAccountDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.PriorThreeDSAuthentication
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.ShippingDetailsUsage
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.TravelDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.UserLogin

data class UserAccountDetails(
    /** Created date. */
    val createdDate: String? = null,

    /** Created range. */
    val createdRange: CreatedRange? = null,

    /** Changed date. */
    val changedDate: String? = null,

    /** Changed range. */
    val changedRange: ChangedRange? = null,

    /** Password changed date. */
    val passwordChangedDate: String? = null,

    /** Password changed range. */
    val passwordChangedRange: PasswordChangeRange? = null,

    /** Total purchases six month count. */
    val totalPurchasesSixMonthCount: Int? = null,

    /** Transaction count for previous day. */
    val transactionCountForPreviousDay: Int? = null,

    /** Transaction count for previous year. */
    val transactionCountForPreviousYear: Int? = null,

    /** Suspicious account activity. */
    val suspiciousAccountActivity: Boolean? = null,

    /** Shipping details usage. */
    val shippingDetailsUsage: ShippingDetailsUsage? = null,

    /** Payment account details. */
    val paymentAccountDetails: PaymentAccountDetails? = null,

    /** User login. */
    val userLogin: UserLogin? = null,

    /** Prior 3DS authentication. */
    val priorThreeDSAuthentication: PriorThreeDSAuthentication? = null,

    /** Travel details. */
    val travelDetails: TravelDetails? = null
)