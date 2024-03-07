/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay

import android.content.Context
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.paysafe.android.core.domain.model.config.PSEnvironment
import com.paysafe.android.google_pay.domain.model.GoogleCardNetwork
import com.paysafe.android.google_pay.domain.model.GoogleMerchantInfo
import com.paysafe.android.paymentmethods.domain.model.GoogleAuthMethod
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

internal object GooglePayManager {

    var allowCreditCards = false

    private val CENTS = BigDecimal(100)

    private fun provideBaseJSON() = JSONObject().apply {
        put("apiVersion", 2)
        put("apiVersionMinor", 0)
    }

    private fun gatewayTokenizationSpecification(merchantId: String) = JSONObject().apply {
        put("type", "PAYMENT_GATEWAY")
        put("parameters", JSONObject(getGatewayTokenizationParameters(merchantId)))
    }

    private fun getGatewayTokenizationParameters(merchantId: String) = mapOf(
        "gateway" to "paysafe",
        "gatewayMerchantId" to merchantId,
    )

    internal fun baseCardPaymentMethod(
        allowedAuthMethods: List<GoogleAuthMethod>,
        allowedCardNetworks: List<GoogleCardNetwork>,
        requestBillingAddress: Boolean
    ) = JSONObject().apply {
        val parameters = JSONObject().apply {
            put("allowedAuthMethods", JSONArray(allowedAuthMethods.map { it.name }))
            put("allowedCardNetworks", JSONArray(allowedCardNetworks.map { it.name }))
            put("allowCreditCards", allowCreditCards)
            if (requestBillingAddress) {
                put("billingAddressRequired", true)
                put("billingAddressParameters", JSONObject().apply {
                    put("format", "FULL")
                    put("phoneNumberRequired", true)
                })
            }
        }

        put("type", "CARD")
        put("parameters", parameters)
    }

    internal fun cardPaymentMethod(
        merchantId: String,
        allowedAuthMethods: List<GoogleAuthMethod>,
        allowedCardNetworks: List<GoogleCardNetwork>,
        requestBillingAddress: Boolean
    ): JSONObject {
        val cardPaymentMethod =
            baseCardPaymentMethod(allowedAuthMethods, allowedCardNetworks, requestBillingAddress)
        cardPaymentMethod.put(
            "tokenizationSpecification",
            gatewayTokenizationSpecification(merchantId)
        )
        return cardPaymentMethod
    }

    fun allowedPaymentMethods(
        merchantId: String,
        allowedAuthMethods: List<GoogleAuthMethod>,
        allowedCardNetworks: List<GoogleCardNetwork>,
        requestBillingAddress: Boolean
    ): JSONArray = JSONArray().put(
        cardPaymentMethod(
            merchantId = merchantId,
            allowedAuthMethods = allowedAuthMethods,
            allowedCardNetworks = allowedCardNetworks,
            requestBillingAddress = requestBillingAddress
        )
    )

    fun isReadyToPayRequest(
        allowedAuthMethods: List<GoogleAuthMethod>,
        allowedCardNetworks: List<GoogleCardNetwork>,
        requestBillingAddress: Boolean
    ) = provideBaseJSON().apply {
        put(
            "allowedPaymentMethods",
            JSONArray().put(
                baseCardPaymentMethod(
                    allowedAuthMethods = allowedAuthMethods,
                    allowedCardNetworks = allowedCardNetworks,
                    requestBillingAddress = requestBillingAddress
                )
            )
        )
    }

    fun createPaymentsClient(
        context: Context,
        environment: PSEnvironment
    ): PaymentsClient {
        val walletEnvironment = getWalletEnvironment(environment)
        val walletOptions =
            Wallet.WalletOptions.Builder()
                .setEnvironment(walletEnvironment)
                .build()

        return Wallet.getPaymentsClient(context, walletOptions)
    }

    fun getPaymentDataRequest(
        priceCents: Long,
        googleMerchantInfo: GoogleMerchantInfo,
        countryCode: String,
        currencyCode: String,
        allowedAuthMethods: List<GoogleAuthMethod>,
        allowedCardNetworks: List<GoogleCardNetwork>,
        requestBillingAddress: Boolean
    ) = provideBaseJSON().apply {
        put(
            "allowedPaymentMethods",
            JSONArray().put(
                cardPaymentMethod(
                    googleMerchantInfo.merchantId,
                    allowedAuthMethods,
                    allowedCardNetworks,
                    requestBillingAddress
                )
            )
        )
        put(
            "transactionInfo",
            getTransactionInfo(priceCents.centsToString(), countryCode, currencyCode)
        )
        put("merchantInfo", getMerchantInfo(googleMerchantInfo.merchantName))
        put("shippingAddressRequired", false)
    }

    internal fun getMerchantInfo(merchantName: String) =
        JSONObject().put("merchantName", merchantName)

    internal fun getTransactionInfo(
        price: String,
        countryCode: String,
        currencyCode: String
    ) = JSONObject().apply {
        put("totalPrice", price)
        put("totalPriceStatus", "FINAL")
        put("countryCode", countryCode)
        put("currencyCode", currencyCode)
    }

    internal fun Long.centsToString() =
        BigDecimal(this).divide(CENTS).setScale(2, RoundingMode.HALF_EVEN).toString()

    private fun getWalletEnvironment(psEnvironment: PSEnvironment) = when (psEnvironment) {
        PSEnvironment.TEST -> WalletConstants.ENVIRONMENT_TEST
        PSEnvironment.PROD -> WalletConstants.ENVIRONMENT_PRODUCTION
    }
}