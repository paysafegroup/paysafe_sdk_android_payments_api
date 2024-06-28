/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RequestorChallengePreferenceSerializable {
    @SerialName("CHALLENGE_MANDATED")
    CHALLENGE_MANDATED,

    @SerialName("CHALLENGE_REQUESTED")
    CHALLENGE_REQUESTED,

    @SerialName("NO_PREFERENCE")
    NO_PREFERENCE
}