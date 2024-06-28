package com.paysafe.android.brainTreeDetails.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BraintreeDetailsRequest(
    @SerialName("payment_method_nonce")
    val paymentMethodNonce: String?,

    @SerialName("payment_method_payerInfo")
    val paymentMethodPayerInfo: PayerInfo?,

    @SerialName("payment_method_jwtToken")
    val paymentMethodJwtToken: String?,

    @SerialName("payment_method_deviceData")
    val paymentMethodDeviceData: DeviceData?,

    @SerialName("errorCode")
    val errorCode: String?
)