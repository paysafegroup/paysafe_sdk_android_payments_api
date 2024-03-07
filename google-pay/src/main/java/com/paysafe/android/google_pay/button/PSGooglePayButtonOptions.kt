/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.button

class PSGooglePayButtonOptions internal constructor(
    builder: Builder,
    internal val paymentMethodConfig: PSGooglePayPaymentMethodConfig
) {
    internal val buttonTheme: PSGooglePayButtonTheme
    internal val buttonType: PSGooglePayButtonType
    internal val cornerRadius: Int

    init {
        this.buttonTheme = builder.buttonTheme ?: PSGooglePayButtonTheme.DARK
        this.buttonType = builder.buttonType ?: PSGooglePayButtonType.BUY
        this.cornerRadius = builder.cornerRadius ?: 100
    }

    class Builder(
        private val paymentMethodConfig: PSGooglePayPaymentMethodConfig
    ) {
        internal var buttonTheme: PSGooglePayButtonTheme? = null
            private set

        internal var buttonType: PSGooglePayButtonType? = null
            private set

        internal var cornerRadius: Int? = null
            private set


        fun buttonTheme(buttonTheme: PSGooglePayButtonTheme) = apply {
            this.buttonTheme = buttonTheme
        }

        fun buttonType(buttonType: PSGooglePayButtonType) = apply {
            this.buttonType = buttonType
        }

        fun cornerRadius(cornerRadius: Int?) = apply {
            this.cornerRadius = cornerRadius
        }


        fun build() = PSGooglePayButtonOptions(this, paymentMethodConfig)
    }

}