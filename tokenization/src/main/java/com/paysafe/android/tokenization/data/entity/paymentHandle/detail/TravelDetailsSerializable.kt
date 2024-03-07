/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.entity.paymentHandle.detail

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TravelDetailsSerializable(
    /** Is air travel. */
    @SerialName("isAirTravel")
    val isAirTravel: Boolean? = null,

    /** Airline carrier. */
    @SerialName("airlineCarrier")
    val airlineCarrier: String? = null,

    /** Departure date. */
    @SerialName("departureDate")
    val departureDate: String? = null,

    /** Destination. */
    @SerialName("destination")
    val destination: String? = null,

    /** Origin. */
    @SerialName("origin")
    val origin: String? = null,

    /** Passenger first name. */
    @SerialName("passengerFirstName")
    val passengerFirstName: String? = null,

    /** Passenger last name. */
    @SerialName("passengerLastName")
    val passengerLastName: String? = null
)