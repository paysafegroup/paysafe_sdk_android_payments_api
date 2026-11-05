package com.paysafe.android.venmo.data

import android.content.Context
import com.braintreepayments.api.datacollector.DataCollector
import com.braintreepayments.api.datacollector.DataCollectorRequest
import com.braintreepayments.api.datacollector.DataCollectorResult

/**
 * Collects Braintree device data for Venmo fraud tools.
 *
 * Location is not shared. This SDK has no signal that the shopper consented to location collection,
 * so [DataCollectorRequest.hasUserLocationConsent] stays false.
 */
internal class BraintreeDeviceDataCollector(
    private val dataCollectorFactory: (Context, String) -> DataCollector = { context, authorization ->
        DataCollector(context, authorization)
    }
) : DeviceDataCollector {

    override fun collect(
        context: Context,
        authorization: String,
        clientMetadataId: String?,
        callback: (Result<String>) -> Unit
    ) {
        val dataCollector = dataCollectorFactory(context, authorization)
        val request = DataCollectorRequest(
            hasUserLocationConsent = false,
            riskCorrelationId = clientMetadataId
        )
        dataCollector.collectDeviceDataOnSuccess(context, request) { result ->
            when (result) {
                is DataCollectorResult.Success -> callback(Result.success(result.deviceData))
                is DataCollectorResult.Failure -> callback(Result.failure(result.error))
            }
        }
    }
}
