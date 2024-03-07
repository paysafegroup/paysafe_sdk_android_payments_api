/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.paypal.domain.mapper

import com.paysafe.android.paypal.domain.model.PSPayPalTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PSPayPalLanguage
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PSPayPalShippingPreference
import com.paysafe.android.tokenization.domain.model.paymentHandle.paypal.PayPalRequest
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PSPayPalTokenizeOptionsMapperTest {

    private val amountInput = 11111
    private val currencyCodeInput = "USD"
    private val transactionTypeInput = TransactionType.PAYMENT
    private val merchantRefNumInput = "87654321"
    private val billingDetailsInput = BillingDetails("USA", "12345")
    private val profileInput = Profile()
    private val accountIdInput = "12345"
    private val merchantDescriptorInput = MerchantDescriptor("dynamic desc")
    private val shippingDetailsInput = ShippingDetails()
    private val consumerIdInput = "consumerIdInput"
    private val consumerMessageInput = "consumerMessageInput"
    private val recipientDescriptionInput = "recipientDescriptionInput"
    private val languageInput = PSPayPalLanguage.ES
    private val orderDescriptionInput = "orderDescriptionInput"
    private val shippingPreferenceInput = PSPayPalShippingPreference.NO_SHIPPING

    @Test
    fun `IF paypal tokenization options contains data THEN toTokenizationOptions RETURNS PSTokenizeOptions with data`() {
        // Arrange
        val input = PSPayPalTokenizeOptions(
            amountInput,
            currencyCodeInput,
            transactionTypeInput,
            merchantRefNumInput,
            billingDetailsInput,
            profileInput,
            accountIdInput,
            merchantDescriptorInput,
            shippingDetailsInput,
            PayPalRequest(
                consumerIdInput,
                recipientDescriptionInput,
                languageInput,
                shippingPreferenceInput,
                consumerMessageInput,
                orderDescriptionInput,
            )
        )
        val returnLinksInput = listOf<PaymentHandleReturnLink>()

        // Act
        val output = input.toPaymentHandleRequest(returnLinksInput)

        // Assert
        assertEquals(amountInput, output.amount)
        assertEquals(currencyCodeInput, output.currencyCode)
        assertEquals(transactionTypeInput, output.transactionType)
        assertEquals(merchantRefNumInput, output.merchantRefNum)
        assertNotNull(output.billingDetails)
        assertNotNull(output.profile)
        assertEquals(accountIdInput, output.accountId)
        assertNotNull(output.merchantDescriptor)
        assertNotNull(output.shippingDetails)
        assertEquals(PaymentType.PAYPAL, output.paymentType)
        assertNotNull(output.payPalRequest)
        assertNotNull(output.returnLinks)
        assertTrue(output.returnLinks?.isEmpty()!!)
    }

}