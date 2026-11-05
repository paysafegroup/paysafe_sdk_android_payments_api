package com.paysafe.android.venmo.data

import android.content.Context

internal fun interface DeviceDataCollector {
    fun collect(
        context: Context,
        authorization: String,
        clientMetadataId: String?,
        callback: (Result<String>) -> Unit
    )
}