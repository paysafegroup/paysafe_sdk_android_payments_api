package com.paysafe.android.brainTreeDetails.data

import androidx.lifecycle.LifecycleOwner
import com.paysafe.android.brainTreeDetails.data.entity.BrainTreeDetailsResponse
import com.paysafe.android.brainTreeDetails.domain.models.BraintreeDetailsRequest
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.paymentmethods.domain.model.PaymentMethod


/**
 * Braintree details interface that defines the methods to retrieve the venmo account nonce details
 */
interface BrainTreeDetailsService {

    /**
     * Definition to be able to get payment methods
     *
     * @param braintreeDetailsRequest The request object required to call the BrainTree details endpoint, it contains:
     *                  "payment_method_nonce"
     *                 "payment_method_jwtToken"
     *                 "payment_method_payerInfo"
     *                 "payment_method_deviceData"
     * @param callback Object to process success and errors methods.
     */
    fun getBraintreeDetails(
        lifecycleOwner: LifecycleOwner,
        braintreeDetailsRequest: BraintreeDetailsRequest,
        callback: PSResultCallback<BrainTreeDetailsResponse>
    )

    /**
     * Definition to be able to get payment methods
     *
     * @param braintreeDetailsRequest The request object required to call the BrainTree details endpoint, it contains:
     *                  "payment_method_nonce"
     *                 "payment_method_jwtToken"
     *                 "payment_method_payerInfo"
     *                 "payment_method_deviceData"
     * @return Paysafe result wrapper object [PSResult] with the [BrainTreeDetailsResponse].
     */
    @JvmSynthetic
    suspend fun getBraintreeDetails(braintreeDetailsRequest: BraintreeDetailsRequest): PSResult<BrainTreeDetailsResponse>

}