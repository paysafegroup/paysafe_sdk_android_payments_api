package com.paysafe.android.tokenization.domain.model.paymentHandle

enum class PaymentHandleAction {
    /** should navigate to 3DS */
    REDIRECT,

    /** there is no required actions */
    NONE
}
