/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.domain.model

sealed interface PSPayPalRenderType {

    data class PSPayPalNativeRenderType(

        /** The application ID (typically referenced via BuildConfig.APPLICATION_ID) to which the
         * Paysafe SDK will append "://paypalpay" in order to form the returnUrl
         * Example: for applicationId "com.app.example" the returnUrl will be "com.app.example://paypalpay" */
        val applicationId: String

    ) : PSPayPalRenderType

    object PSPayPalWebRenderType : PSPayPalRenderType

}
