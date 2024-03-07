/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.core.util

import android.util.Log
import com.paysafe.android.core.BuildConfig

object LocalLog {
    fun d(tag: String?, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    fun i(tag: String?, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    fun e(tag: String?, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }
}