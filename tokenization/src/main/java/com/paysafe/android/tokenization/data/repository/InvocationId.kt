/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.data.repository

import com.paysafe.android.tokenization.domain.repository.UniversallyUniqueId
import java.util.UUID

class InvocationId : UniversallyUniqueId {
    override fun generate() = UUID.randomUUID().toString().lowercase()
}