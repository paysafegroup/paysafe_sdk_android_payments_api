package com.paysafe.android.venmo.domain.mapper

import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentHandleReturnLink
import com.paysafe.android.tokenization.domain.model.paymentHandle.PaymentType
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.venmo.VenmoRequest
import com.paysafe.android.venmo.domain.model.PSVenmoTokenizeOptions
import org.junit.Assert
import org.junit.Test

class PSVenmoTokenizeOptionsMapperTest {

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
    private val profileId = "profileId"
    private val recipientDescriptionInput = "recipientDescriptionInput"

    @Test
    fun `IF venmo tokenization options contains data THEN toTokenizationOptions RETURNS PSTokenizeOptions with data`() {
        // Arrange
        val input = PSVenmoTokenizeOptions(
            amountInput,
            currencyCodeInput,
            transactionTypeInput,
            merchantRefNumInput,
            billingDetailsInput,
            profileInput,
            accountIdInput,
            merchantDescriptorInput,
            shippingDetailsInput,
            VenmoRequest(
                consumerIdInput,
                recipientDescriptionInput,
                profileId
            )
        )
        val returnLinksInput = listOf<PaymentHandleReturnLink>()

        // Act
        val output = input.toPaymentHandleRequest(returnLinksInput)

        // Assert
        Assert.assertEquals(amountInput, output.amount)
        Assert.assertEquals(currencyCodeInput, output.currencyCode)
        Assert.assertEquals(transactionTypeInput, output.transactionType)
        Assert.assertEquals(merchantRefNumInput, output.merchantRefNum)
        Assert.assertNotNull(output.billingDetails)
        Assert.assertNotNull(output.profile)
        Assert.assertEquals(accountIdInput, output.accountId)
        Assert.assertNotNull(output.merchantDescriptor)
        Assert.assertNotNull(output.shippingDetails)
        Assert.assertEquals(PaymentType.VENMO, output.paymentType)
        Assert.assertNotNull(output.venmoRequest)
        Assert.assertNotNull(output.returnLinks)
        Assert.assertTrue(output.returnLinks?.isEmpty()!!)
    }
}