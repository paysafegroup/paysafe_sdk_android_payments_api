/*
 * Copyright (c) 2026 Paysafe Group
 */

package com.paysafe.android.threedsecure

import androidx.fragment.app.FragmentActivity
import com.cardinalcommerce.cardinalmobilesdk.Cardinal
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalChallengeObserver
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver

class CardinalChallengeManager(
    private val activity: FragmentActivity,
) {

    private var challengeObserver: CardinalChallengeObserver? = null
    private var validateReceiver: CardinalValidateReceiver? = null
    private var validateReceiverCallback: ((validateResponse: ValidateResponse, serverJwt: String?) -> Unit)? =
        null

    fun setValidateReceiverCallback(callback: (validateResponse: ValidateResponse, serverJwt: String?) -> Unit) {
        validateReceiverCallback = callback
    }

    fun initObserver() {
        validateReceiver = CardinalValidateReceiver { _, validateResponse, serverJwt ->
            validateReceiverCallback?.invoke(validateResponse, serverJwt)
        }
        challengeObserver = validateReceiver?.let { CardinalChallengeObserver(activity, it) }
    }

    fun continue3DS(jwt: String, payload: String) {
        val observer = challengeObserver
            ?: throw IllegalStateException("CardinalChallengeObserver not initialized yet")

        Cardinal.getInstance().cca_continue(jwt, payload, observer)
    }

    fun cleanup() {
        validateReceiverCallback = null
        challengeObserver = null
        validateReceiver = null
    }
}