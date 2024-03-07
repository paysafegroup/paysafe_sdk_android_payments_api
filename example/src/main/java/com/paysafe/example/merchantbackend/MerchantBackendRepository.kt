/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.merchantbackend

import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.example.merchantbackend.data.domain.SingleUseCustomerTokens

interface MerchantBackendRepository {

    suspend fun requestSingleUseCustomerTokens(): PSResult<SingleUseCustomerTokens>
}