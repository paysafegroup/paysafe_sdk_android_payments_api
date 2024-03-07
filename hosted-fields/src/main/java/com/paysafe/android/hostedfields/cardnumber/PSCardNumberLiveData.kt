/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.android.hostedfields.cardnumber

import androidx.lifecycle.MutableLiveData
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType

data class PSCardNumberLiveData(
    val cardTypeLiveData: MutableLiveData<PSCreditCardType>,
    val isValidLiveData: MutableLiveData<Boolean>
)
