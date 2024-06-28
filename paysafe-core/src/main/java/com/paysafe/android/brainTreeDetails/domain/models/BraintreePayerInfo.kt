package com.paysafe.android.brainTreeDetails.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PayerInfo(
    @SerialName("firstName")
    val firstName: String,

    @SerialName("lastName")
    val lastName: String,

    @SerialName("phoneNumber")
    val phoneNumber: String,

    @SerialName("email")
    val email: String,

    @SerialName("externalId")
    val externalId: String,

    @SerialName("userName")
    val userName: String
)