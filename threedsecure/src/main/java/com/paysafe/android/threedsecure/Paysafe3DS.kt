/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.threedsecure


import android.app.Activity
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.core.data.entity.PSResultCallback
import com.paysafe.android.core.data.entity.resultAsCallback
import com.paysafe.android.threedsecure.data.api.ThreeDSecureApi
import com.paysafe.android.threedsecure.data.repository.ThreeDSecureRepositoryImpl
import com.paysafe.android.threedsecure.domain.model.ThreeDSChallengePayload
import com.paysafe.android.threedsecure.domain.model.ThreeDSRenderType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class Paysafe3DS internal constructor(
    private val mainDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher
) : Paysafe3DSService {

    constructor() : this(
        mainDispatcher = Dispatchers.Main,
        ioDispatcher = Dispatchers.IO
    )

    private val httpClient
        get() = PaysafeSDK.getPSApiClient()
    private val controller: Paysafe3DSController
    private val jwtApi = ThreeDSecureApi(httpClient)

    private fun buildPaysafeThreeDSecureController(): Paysafe3DSController {
        val threeDSecureJwtRepository = ThreeDSecureRepositoryImpl(jwtApi, httpClient)
        val cardinal = Cardinal.getInstance()
        val json = Json {
            ignoreUnknownKeys = true
        }
        return Paysafe3DSController(
            cardinal,
            json,
            threeDSecureJwtRepository
        )
    }

    init {
        controller = buildPaysafeThreeDSecureController()
    }

    override fun start(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        cardBin: String,
        accountId: String,
        threeDSRenderType: ThreeDSRenderType?,
        callback: PSResultCallback<String>
    ) {
        lifecycleOwner.lifecycleScope.launch(ioDispatcher) {
            val result = start(context, cardBin, accountId, threeDSRenderType)
            withContext(mainDispatcher) {
                resultAsCallback(result, callback)
            }
        }
    }

    override fun launch3dsChallenge(
        activity: Activity,
        challengePayload: String,
        callback: PSResultCallback<ThreeDSChallengePayload>
    ) {
        val lifecycleOwner = activity as LifecycleOwner
        lifecycleOwner.lifecycleScope.launch(ioDispatcher) {
            val result = launch3dsChallenge(activity, challengePayload)
            withContext(mainDispatcher) {
                resultAsCallback(result, callback)
            }
        }
    }

    override suspend fun start(
        context: Context,
        bin: String,
        accountId: String,
        threeDSRenderType: ThreeDSRenderType?
    ) = controller.initCardinal3DS(
        context = context,
        bin = bin,
        accountId = accountId,
        threeDSRenderType = threeDSRenderType,
        psApiClient = httpClient
    )

    override suspend fun launch3dsChallenge(
        activity: Activity,
        challengePayload: String
    ): PSResult<ThreeDSChallengePayload> = controller.continueCardinal3DSChallenge(
        activity, challengePayload, httpClient
    )

    override fun dispose() = controller.disposeCardinal()

}
