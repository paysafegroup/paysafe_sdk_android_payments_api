package com.paysafe.android.brainTreeDetails.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BraintreeDetailsRequest(
    @SerialName("payment_method_nonce")
    val paymentMethodNonce: String? = null,

    @SerialName("payment_method_payerInfo")
    val paymentMethodPayerInfo: PayerInfo? = null,

    @SerialName("payment_method_jwtToken")
    val paymentMethodJwtToken: String? = null,

    @SerialName("payment_method_deviceData")
    val paymentMethodDeviceData: DeviceData? = null,

    @SerialName("errorCode")
    val errorCode: String? = null
)