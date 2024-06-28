/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.data.entity

/**
 * Model for generic data required for each Paysafe request.
 */
data class PSApiRequest(
    /** Paysafe api request type. */
    internal val requestType: PSApiRequestType,

    /** Request path for api call. */
    internal val path: String,

    /** Request headers for api call. */
    internal val headers: Map<String, String> = emptyMap(),

    /** Query parameters for api call. */
    internal val queryParams: Map<String, String?> = emptyMap(),

    /** Body for api call. */
    var body: String? = null,
)

/** Http request types supported, GET and POST. */
enum class PSApiRequestType { GET, POST }
