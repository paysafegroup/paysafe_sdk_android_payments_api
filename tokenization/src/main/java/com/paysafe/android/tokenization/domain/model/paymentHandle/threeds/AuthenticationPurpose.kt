/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.tokenization.domain.model.paymentHandle.threeds

enum class AuthenticationPurpose {
    PAYMENT_TRANSACTION,
    RECURRING_TRANSACTION,
    INSTALMENT_TRANSACTION,
    ADD_CARD,
    MAINTAIN_CARD,
    EMV_TOKEN_VERIFICATION
}