package com.paysafe.android.venmo.activity

class VenmoServiceConstants {
    fun getIntentExtraSessionToken(): String {
        return VenmoConstants.INTENT_EXTRA_SESSION_TOKEN
    }

    fun getIntentExtraClientToken(): String {
        return VenmoConstants.INTENT_EXTRA_CLIENT_TOKEN
    }

    fun getIntentExtraCustomUrlSchemeToken(): String {
        return VenmoConstants.INTENT_EXTRA_CUSTOM_URL_SCHEME
    }

    fun getIntentExtraProfileId(): String {
        return VenmoConstants.INTENT_EXTRA_PROFILE_ID
    }

    fun getIntentExtraAmount(): String {
        return VenmoConstants.INTENT_EXTRA_AMOUNT
    }

    fun getResultSuccess(): Any {
        return VenmoConstants.RESULT_SUCCESS
    }

    fun getResultFailed(): Any {
        return VenmoConstants.RESULT_FAILED
    }
}