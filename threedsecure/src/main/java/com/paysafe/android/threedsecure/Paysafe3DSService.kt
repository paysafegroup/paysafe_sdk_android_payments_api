/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.threedsecure.domain.model.ThreeDSChallengePayload
import com.paysafe.android.threedsecure.domain.model.ThreeDSRenderType

/**
 * Contract for the Paysafe 3DS, focused mainly in start and launch challenge.
 */
interface Paysafe3DSService {

    /**
     * Initialize the 3DS challenge SDK.
     *
     * @param lifecycleOwner Context that owns a lifecycle. Most of the cases an [Activity].
     * @param cardBin Card bank identification number.
     * @param callback Result object with success/failure methods to handle 3DS initialization.
     */
    fun start(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        cardBin: String,
        accountId: String,
        threeDSRenderType: ThreeDSRenderType?,
        callback: PSResultCallback<String>
    )

    /**
     * Launch the 3DS challenge request.
     *
     * @param activity Reference for android framework activity.
     * @param challengePayload 3DS challenge payload.
     * @param callback Result object with success/failure methods to handle 3DS challenge payload.
     */
    fun launch3dsChallenge(
        activity: Activity,
        challengePayload: String,
        callback: PSResultCallback<ThreeDSChallengePayload>
    )

    /**
     * Coroutine to initialize the 3DS challenge SDK.
     *
     * @param bin Bank identification number.
     * @return Device finger print ID.
     */
    @JvmSynthetic
    suspend fun start(
        context: Context,
        bin: String,
        accountId: String,
        threeDSRenderType: ThreeDSRenderType?
    ): PSResult<String>

    /**
     * Coroutine to launch the 3DS challenge request.
     *
     * @param activity Reference for android framework activity.
     * @param challengePayload 3DS challenge payload.
     * @return Challenge payload wrapped in [PSResult].
     */
    @JvmSynthetic
    suspend fun launch3dsChallenge(
        activity: Activity,
        challengePayload: String
    ): PSResult<ThreeDSChallengePayload>

    /**
     * Disposes library resources. WARNING: Only use this when 3DS challenge is no longer used.
     */
    fun dispose()

}