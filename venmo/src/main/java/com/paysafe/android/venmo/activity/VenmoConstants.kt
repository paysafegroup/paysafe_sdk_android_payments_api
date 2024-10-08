package com.paysafe.android.venmo.activity

object VenmoConstants {
    const val INTENT_EXTRA_SESSION_TOKEN = "SESSION_TOKEN"
    const val INTENT_EXTRA_CLIENT_TOKEN = "CLIENT_TOKEN"
    const val INTENT_EXTRA_PROFILE_ID = "PROFILE_ID"
    const val INTENT_EXTRA_CUSTOM_URL_SCHEME = "CUSTOM_URL_SCHEME"
    const val INTENT_EXTRA_AMOUNT = "AMOUNT"
    const val RESULT_SUCCESS = 1_000
    const val RESULT_FAILED = 1_001
    const val RESULT_VENMO_APP_IS_NOT_INSTALLED = 1_002
    const val VENMO_PACKAGE = "com.venmo"
}