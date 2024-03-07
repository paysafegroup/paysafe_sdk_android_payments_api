/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.ChangedRangeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.CreatedRangeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.PasswordChangeRangeSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.PaymentAccountDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.PriorThreeDSAuthenticationSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.ShippingDetailsUsageSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.TravelDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.UserLoginSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserAccountDetailsSerializable(

    /** Created date. */
    @SerialName("createdDate")
    val createdDate: String? = null,

    /** Created range. */
    @SerialName("createdRange")
    val createdRange: CreatedRangeSerializable? = null,

    /** Changed date. */
    @SerialName("changedDate")
    val changedDate: String? = null,

    /** Changed range. */
    @SerialName("changedRange")
    val changedRange: ChangedRangeSerializable? = null,

    /** Password changed date. */
    @SerialName("passwordChangedDate")
    val passwordChangedDate: String? = null,

    /** Password changed range. */
    @SerialName("passwordChangedRange")
    val passwordChangedRange: PasswordChangeRangeSerializable? = null,

    /** Total purchases six month count. */
    @SerialName("totalPurchasesSixMonthCount")
    val totalPurchasesSixMonthCount: Int? = null,

    /** Transaction count for previous day. */
    @SerialName("transactionCountForPreviousDay")
    val transactionCountForPreviousDay: Int? = null,

    /** Transaction count for previous year. */
    @SerialName("transactionCountForPreviousYear")
    val transactionCountForPreviousYear: Int? = null,

    /** Suspicious account activity. */
    @SerialName("suspiciousAccountActivity")
    val suspiciousAccountActivity: Boolean? = null,

    /** Shipping details usage. */
    @SerialName("shippingDetailsUsage")
    val shippingDetailsUsage: ShippingDetailsUsageSerializable? = null,

    /** Payment account details. */
    @SerialName("paymentAccountDetails")
    val paymentAccountDetails: PaymentAccountDetailsSerializable? = null,

    /** User login. */
    @SerialName("userLogin")
    val userLogin: UserLoginSerializable? = null,

    /** Prior 3DS authentication. */
    @SerialName("priorThreeDSAuthentication")
    val priorThreeDSAuthentication: PriorThreeDSAuthenticationSerializable? = null,

    /** Travel details. */
    @SerialName("travelDetails")
    val travelDetails: TravelDetailsSerializable? = null

)