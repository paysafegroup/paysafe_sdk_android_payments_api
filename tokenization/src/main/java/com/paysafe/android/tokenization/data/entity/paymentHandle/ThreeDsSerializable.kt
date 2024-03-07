/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.PriorThreeDSAuthenticationSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.ShippingDetailsUsageSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.TravelDetailsSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.detail.UserLoginSerializable
import com.paysafe.android.tokenization.data.entity.paymentHandle.request.OrderItemDetailsSerializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure to store Three DS information data.
 */
@Serializable
internal data class ThreeDSSerializable(

    /** Merchant ref number for 3D secure. */
    @SerialName("merchantRefNum")
    val merchantRefNum: String? = null,

    /** Merchant url for 3D secure. */
    @SerialName("merchantUrl")
    val merchantUrl: String,

    /** Device channel for 3D secure. */
    @SerialName("deviceChannel")
    val deviceChannel: String,

    /** Message category for 3D secure. */
    @SerialName("messageCategory")
    val messageCategory: MessageCategorySerializable,

    /** Transaction intent for 3D secure. */
    @SerialName("transactionIntent")
    val transactionIntent: TransactionIntentSerializable,

    /** Authentication purpose for 3D secure. */
    @SerialName("authenticationPurpose")
    val authenticationPurpose: AuthenticationPurposeSerializable,

    @SerialName("billingCycle")
    val billingCycle: BillingCycleSerializable? = null,

    /** Requestor challenge preference for 3D secure. */
    @SerialName("requestorChallengePreference")
    val requestorChallengePreference: RequestorChallengePreferenceSerializable? = null,

    /** User login for 3D secure. */
    @SerialName("userLogin")
    val userLogin: UserLoginSerializable? = null,

    /** Order item details for 3D secure. */
    @SerialName("orderItemDetails")
    val orderItemDetails: OrderItemDetailsSerializable? = null,

    /** Purchased gift card details for 3D secure. */
    @SerialName("purchasedGiftCardDetails")
    val purchasedGiftCardDetails: PurchasedGiftCardDetailsSerializable? = null,

    /** User account details for 3D secure. */
    @SerialName("userAccountDetails")
    val userAccountDetails: UserAccountDetailsSerializable? = null,

    /** Prior 3DS authentication. */
    @SerialName("priorThreeDSAuthentication")
    val priorThreeDSAuthentication: PriorThreeDSAuthenticationSerializable? = null,

    /** Shipping details usage. */
    @SerialName("shippingDetailsUsage")
    val shippingDetailsUsage: ShippingDetailsUsageSerializable? = null,

    /** Suspicious account activity. */
    @SerialName("suspiciousAccountActivity")
    val suspiciousAccountActivity: Boolean? = null,

    /** Total purchases six month count. */
    @SerialName("totalPurchasesSixMonthCount")
    val totalPurchasesSixMonthCount: Int? = null,

    /** Transaction count for previous day. */
    @SerialName("transactionCountForPreviousDay")
    val transactionCountForPreviousDay: Int? = null,

    /** Transaction count for previous year. */
    @SerialName("transactionCountForPreviousYear")
    val transactionCountForPreviousYear: Int? = null,

    /** Travel details for 3D secure. */
    @SerialName("travelDetails")
    val travelDetails: TravelDetailsSerializable? = null,

    /** Max authorization for instalment payment. */
    @SerialName("maxAuthorizationsForInstalmentPayment")
    val maxAuthorizationsForInstalmentPayment: Int? = null,

    /** Electronic delivery. */
    @SerialName("electronicDelivery")
    val electronicDelivery: ElectronicDeliverySerializable? = null,

    /** Initial purchase time. */
    @SerialName("initialPurchaseTime")
    val initialPurchaseTime: String? = null,

    @SerialName("useThreeDSecureVersion2")
    val useThreeDSecureVersion2: Boolean? = null,

    @SerialName("profile")
    val threeDSProfile: ThreeDSProfileSerializable? = null

)