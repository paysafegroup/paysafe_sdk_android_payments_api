/*
 * Copyright (c) 2024 Paysafe Group 
 */

package com.paysafe.android.hostedfields.domain.mapper

import com.paysafe.android.hostedfields.domain.model.PSCardTokenizeOptions
import com.paysafe.android.hostedfields.domain.model.RenderType
import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.ThreeDS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PSCardTokenizeOptionsMapperTest {

    @Test
    fun `IF PSCardTokenizeOptions contains data THEN toPaymentHandleRequest RETURNS PaymentHandleRequest`() {
        // Arrange
        val amount = 100
        val currencyCode = "USD"
        val transactionType = TransactionType.PAYMENT
        val merchantRefNum = "12345678"
        val billingDetails = BillingDetails("USA", "12345")
        val profile = Profile()
        val accountId = "12345"
        val merchantDescriptor = MerchantDescriptor(
            dynamicDescriptor = "dynamicDescriptor"
        )
        val shippingDetails = ShippingDetails()
        val singleUseCustomerToken = "singleUseCustomerToken"
        val paymentHandleTokenFrom = "paymentHandleTokenFrom"
        val renderType = RenderType.HTML
        val threeDS = ThreeDS(
            merchantUrl = "merchantUrl"
        )
        val input = PSCardTokenizeOptions(
            amount = amount,
            currencyCode = currencyCode,
            transactionType = transactionType,
            merchantRefNum = merchantRefNum,
            billingDetails = billingDetails,
            profile = profile,
            accountId = accountId,
            merchantDescriptor = merchantDescriptor,
            shippingDetails = shippingDetails,
            singleUseCustomerToken = singleUseCustomerToken,
            paymentHandleTokenFrom = paymentHandleTokenFrom,
            renderType = renderType,
            threeDS = threeDS
        )

        // Act
        val output = input.toPaymentHandleRequest()

        // Assert
        assertEquals(amount, output.amount)
        assertEquals(currencyCode, output.currencyCode)
        assertEquals(transactionType, output.transactionType)
        assertEquals(merchantRefNum, output.merchantRefNum)
        assertEquals(billingDetails, output.billingDetails)
        assertEquals(profile, output.profile)
        assertEquals(accountId, output.accountId)
        assertEquals(merchantDescriptor, output.merchantDescriptor)
        assertEquals(shippingDetails, output.shippingDetails)
        assertEquals(PaymentType.CARD, output.paymentType)
        assertEquals(singleUseCustomerToken, output.singleUseCustomerToken)
        assertEquals(paymentHandleTokenFrom, output.paymentHandleTokenFrom)
        assertEquals(threeDS, output.threeDS)
        assertNull(output.googlePayPaymentToken)
        assertNull(output.venmoRequest)
        assertNull(output.returnLinks)
    }

}