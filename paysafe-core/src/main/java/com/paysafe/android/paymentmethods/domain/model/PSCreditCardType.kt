/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paymentmethods.domain.model

/**
 * This enumeration is a named list of credit card types supported. [UNKNOWN] doesn't mean incorrect,
 * it's for valid(luhn algorithm) credit cards, but trademark icon will not be displayed in the
 * user interface.
 */
enum class PSCreditCardType(val value: String) {
    UNKNOWN("UNKNOWN"),
    VISA("VI"),
    MASTERCARD("MC"),
    AMEX("AM"),
    DISCOVER("DI"),
    JCB("JC"),
    MAESTRO("MD"),
    SOLO("SO"),
    VISA_DEBIT("VD"),
    VISA_ELECTRON("VE")
}