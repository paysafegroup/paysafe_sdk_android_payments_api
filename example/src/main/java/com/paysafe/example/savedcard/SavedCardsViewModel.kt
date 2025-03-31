/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.savedcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paysafe.android.core.data.entity.PSResult
import com.paysafe.android.hostedfields.R
import com.paysafe.android.paymentmethods.domain.model.PSCreditCardType
import com.paysafe.example.merchantbackend.MerchantBackendRepository
import com.paysafe.example.merchantbackend.MerchantBackendRepositoryImpl
import com.paysafe.example.merchantbackend.data.domain.payment.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SavedCardsViewModel : ViewModel() {

    private val repository: MerchantBackendRepository = MerchantBackendRepositoryImpl()

    private val _savedCardsLiveData = MutableStateFlow<SavedCardsUiState>(SavedCardsUiState.LOADING)
    val savedCardsLiveData: StateFlow<SavedCardsUiState> = _savedCardsLiveData

    fun onRequestSingleUseCustomerTokens() {
        viewModelScope.launch {
            when (val result = repository.requestSingleUseCustomerTokens()) {
                is PSResult.Success -> {
                    val composeSevedCard = UiSavedCardData(
                        R.drawable.ic_cc_mastercard,
                        PSCreditCardType.MASTERCARD,
                        lastDigits = "0000",
                        holderName = "Compose saved card",
                        expiryMonth = "12",
                        expiryYear = "2099",
                        expiryDate = "12-2099",
                        paymentHandleTokenFrom = "Cmfy9rokKZRyFmI",
                        singleUseCustomerToken = "SP5PhDcXzlI8qEoP"
                    )
                    val data = result.value?.paymentHandles?.map {
                        it.toUI(result.value?.singleUseCustomerToken)
                    }?.toMutableList()?.apply {
                        add(composeSevedCard)
                    }

                    _savedCardsLiveData.value = SavedCardsUiState.SUCCESS(data ?: emptyList())
                }

                is PSResult.Failure -> {
                    _savedCardsLiveData.value = SavedCardsUiState.FAILURE(result.exception)
                }
            }
        }
    }
}

sealed class SavedCardsUiState {
    data class SUCCESS(val data: List<UiSavedCardData>) : SavedCardsUiState()
    data class FAILURE(val exception: Exception) : SavedCardsUiState()

    object LOADING : SavedCardsUiState()
}