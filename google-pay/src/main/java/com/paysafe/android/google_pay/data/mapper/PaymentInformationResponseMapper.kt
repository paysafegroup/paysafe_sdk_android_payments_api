/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.google_pay.data.mapper

import com.paysafe.android.google_pay.data.model.BillingAddressSerializable
import com.paysafe.android.google_pay.data.model.PaymentInformationResponse
import com.paysafe.android.google_pay.data.model.PaymentMethodDataInfoSerializable
import com.paysafe.android.google_pay.data.model.PaymentMethodDataSerializable
import com.paysafe.android.google_pay.data.model.TokenizationDataSerializable
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GoogleBillingAddress
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GoogleCardInfo
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePayPaymentToken
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GooglePaymentMethodData
import com.paysafe.android.tokenization.domain.model.paymentHandle.googlepay.GoogleTokenizationData

internal fun PaymentInformationResponse.toDomain() = GooglePayPaymentToken(
    apiVersion = apiVersion,
    apiVersionMinor = apiVersionMinor,
    googlePaymentMethodData = paymentMethodData?.toDomain(),
)

internal fun PaymentMethodDataSerializable.toDomain() = GooglePaymentMethodData(
    description = description,
    cardInfo = info?.toDomain(),
    tokenizationData = tokenizationData?.toDomain(),
    type = type
)

internal fun PaymentMethodDataInfoSerializable.toDomain() = GoogleCardInfo(
    billingAddress = billingAddress?.toDomain(),
    cardDetails = cardDetails,
    cardNetwork = cardNetwork
)

internal fun BillingAddressSerializable.toDomain() =
    GoogleBillingAddress(
        name = name,
        postalCode = postalCode,
        countryCode = countryCode,
        phoneNumber = phoneNumber,
        address1 = address1,
        address2 = address2,
        address3 = address3,
        locality = locality,
        administrativeArea = administrativeArea,
        sortingCode = sortingCode
    )

internal fun TokenizationDataSerializable.toDomain() = GoogleTokenizationData(
    token = token,
    type = type
)