/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example

import android.app.Application
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.core.util.LocalLog
import com.paysafe.example.util.Consts

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            PaysafeSDK.setup(Consts.API_KEY, Consts.environment)
        } catch (paysafeException: PaysafeException) {
            LocalLog.e(
                "MainApplication",
                "PaysafeSDK.setup() failed with: ${paysafeException.detailedMessage}"
            )
        }
    }

}
