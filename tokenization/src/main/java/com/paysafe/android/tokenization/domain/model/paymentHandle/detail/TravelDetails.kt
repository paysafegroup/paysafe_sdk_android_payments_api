/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.detail

data class TravelDetails(
    /** Is air travel. */
    val isAirTravel: Boolean? = null,

    /** Airline carrier. */
    val airlineCarrier: String? = null,

    /** Departure date. */
    val departureDate: String? = null,

    /** Destination. */
    val destination: String? = null,

    /** Origin. */
    val origin: String? = null,

    /** Passenger first name. */
    val passengerFirstName: String? = null,

    /** Passenger last name. */
    val passengerLastName: String? = null
)