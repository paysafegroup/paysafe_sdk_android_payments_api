/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.threeds

import com.paysafe.android.tokenization.domain.model.paymentHandle.ThreeDSProfile
import com.paysafe.android.tokenization.domain.model.paymentHandle.UserAccountDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.PriorThreeDSAuthentication
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.ShippingDetailsUsage
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.TravelDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.detail.UserLogin

/**
 * ThreeDS data.
 */
data class ThreeDS(

    /** Merchant url for 3D secure. */
    val merchantUrl: String,

    /** Use 3DS version 2. */
    val useThreeDSecureVersion2: Boolean? = null,

    /** Authentication purpose for 3D secure. */
    val authenticationPurpose: AuthenticationPurpose = AuthenticationPurpose.PAYMENT_TRANSACTION,

    /** This is an indicator representing whether to call authenticate end point or not. */
    val process: Boolean? = null,

    /** Max authorizations for instalment payment. */
    val maxAuthorizationsForInstalmentPayment: Int? = null,

    /** Billing cycle. */
    val billingCycle: BillingCycle? = null,

    /** Electronic delivery. */
    val electronicDelivery: ElectronicDelivery? = null,

    /** Profile. */
    val threeDSProfile: ThreeDSProfile? = null,

    /** Message category for 3D secure. */
    val messageCategory: MessageCategory = MessageCategory.PAYMENT,

    /** Requestor challenge preference for 3D secure. */
    val requestorChallengePreference: RequestorChallengePreference? = null,

    /** User login. */
    val userLogin: UserLogin? = null,

    /** Transaction intent for 3D secure. */
    val transactionIntent: TransactionIntent = TransactionIntent.GOODS_OR_SERVICE_PURCHASE,

    /** Initial purchase time. */
    val initialPurchaseTime: String? = null,

    /** Order item details. */
    val orderItemDetails: OrderItemDetails? = null,

    /** Purchased gift card details. */
    val purchasedGiftCardDetails: PurchasedGiftCardDetails? = null,

    /** User account details. */
    val userAccountDetails: UserAccountDetails? = null,

    /** Prior 3DS authentication. */
    val priorThreeDSAuthentication: PriorThreeDSAuthentication? = null,

    /** Shipping details usage. */
    val shippingDetailsUsage: ShippingDetailsUsage? = null,

    /** Suspicious account activity. */
    val suspiciousAccountActivity: Boolean? = null,

    /** Total purchases six month count. */
    val totalPurchasesSixMonthCount: Int? = null,

    /** Transaction count for previous day. */
    val transactionCountForPreviousDay: Int? = null,

    /** Transaction count for previous year. */
    val transactionCountForPreviousYear: Int? = null,

    /** Travel details. */
    val travelDetails: TravelDetails? = null

)