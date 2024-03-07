/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.button

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton
import com.paysafe.android.google_pay.GooglePayManager

class PSGooglePayButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    internal var payButton = PayButton(context)
    private var onClickListener: OnClickListener? = null

    init {
        addView(payButton)
    }

    override fun onClick(v: View?) {
        if (onClickListener != null && v == payButton) {
            payButton.onClick(this)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        onClickListener = l
        payButton.setOnClickListener(l)
    }

    fun initialize(options: PSGooglePayButtonOptions) {
        val allowedPaymentMethods = with(options.paymentMethodConfig) {
            GooglePayManager.allowedPaymentMethods(
                merchantId = merchantId,
                allowedAuthMethods = allowedAuthMethods,
                allowedCardNetworks = allowedCardNetworks,
                requestBillingAddress = requestBillingAddress
            )
        }

        payButton.initialize(
            ButtonOptions.newBuilder()
                .setButtonTheme(payButtonTheme(options.buttonTheme).value)
                .setButtonType(payButtonType(options.buttonType).value)
                .setCornerRadius(options.cornerRadius)
                .setAllowedPaymentMethods(allowedPaymentMethods.toString())
                .build()
        )
    }

}