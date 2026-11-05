package com.paysafe.android.venmo

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

private const val PENDING_REQUEST_PREFS = "venmo_pending_request_prefs"
private const val PENDING_REQUEST_KEY = "venmo_pending_request"
private const val DEVICE_DATA_KEY = "venmo_device_data"

internal class VenmoRequestStorageManage {

    internal fun store(context: Context, pendingRequest: String) {
        context.getSharedPreferences(PENDING_REQUEST_PREFS, MODE_PRIVATE)
            .edit {
                putString(PENDING_REQUEST_KEY, pendingRequest)
            }
    }

    internal fun fetch(context: Context): String? {
        return context.getSharedPreferences(PENDING_REQUEST_PREFS, MODE_PRIVATE)
            .getString(PENDING_REQUEST_KEY, null)
    }

    internal fun clear(context: Context) {
        context.getSharedPreferences(PENDING_REQUEST_PREFS, MODE_PRIVATE)
            .edit {
                remove(PENDING_REQUEST_KEY)
                remove(DEVICE_DATA_KEY)
            }
    }
}