/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.logging.domain.model

/**
 * Types of logging events.
 */
enum class LogType {

    /** Used to log all major events resulting in state change of the SDK including successful
     * invocation of API like SDK is initialized, tokenization is invoked, paymentMethods API call
     * is successful, Cardinal commerce device fingerprinting is completed. */
    CONVERSION,

    /** Used to log error events occurring in the SDK and the error codes returned. */
    ERROR,

    /** Used to log Google Pay related errors like merchant domain is not validated with Google
     * or Mobile device doesn't support Google Pay.  */
    WARNING

}
