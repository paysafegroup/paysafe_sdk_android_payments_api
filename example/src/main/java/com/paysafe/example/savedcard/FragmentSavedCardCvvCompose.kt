/*
 * Copyright (c) 2025 Paysafe Group
 */

package com.paysafe.example.savedcard

import SavedCardCvvScreen
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.util.launchCatching
import com.paysafe.android.hostedfields.PSCardFormConfig
import com.paysafe.android.hostedfields.PSCardFormController
import com.paysafe.android.hostedfields.cvv.PSCvvView
import com.paysafe.android.hostedfields.domain.model.PSCardTokenizeOptions
import com.paysafe.android.hostedfields.domain.model.RenderType
import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.ShippingMethod
import com.paysafe.android.tokenization.domain.model.paymentHandle.TransactionType
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.DateOfBirth
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Gender
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.IdentityDocument
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.Profile
import com.paysafe.android.tokenization.domain.model.paymentHandle.profile.ProfileLocale
import com.paysafe.android.tokenization.domain.model.paymentHandle.threeds.ThreeDS
import com.paysafe.example.R
import com.paysafe.example.successful.SuccessDisplay
import com.paysafe.example.util.Consts
import com.paysafe.example.util.ErrorHandlingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentSavedCardCvvCompose : Fragment() {

    private val args: FragmentSavedCardCvvArgs by navArgs()
    private val navController by lazy {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_sample_app) as NavHostFragment
        navHostFragment.navController
    }

    private lateinit var cardController: PSCardFormController

    private var cardTokenizeOptionsAccountId = ""
    private var cardTokenizeOptionsMerchantRefNum = ""

    private val isPlaceOrderEnabled = mutableStateOf(false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val context = requireContext()
        val cvvView = PSCvvView(context)

        PSCardFormController.initialize(
            cardFormConfig = PSCardFormConfig(
                currencyCode = "USD",
                accountId = Consts.CARDS_ACCOUNT_ID
            ),
            cardCvvView = cvvView,
            callback = object : PSCallback<PSCardFormController> {
                override fun onSuccess(value: PSCardFormController) {
                    cardController = value
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED) {
                            cvvView.isValidLiveData.observe(viewLifecycleOwner) { isEnabled ->
                                isPlaceOrderEnabled.value = isEnabled
                            }
                        }
                    }
                }

                override fun onFailure(exception: Exception) {
                    if (isAdded) {
                        ErrorHandlingDialog.newInstance(
                            exception = exception,
                            title = "CardForm init error"
                        ).show(parentFragmentManager, ErrorHandlingDialog.TAG)
                    } else {
                        Log.w(
                            "FragmentSavedCardCvvCompose",
                            "Fragment is not attached, skipping error dialog."
                        )
                    }
                }
            }
        )

        return ComposeView(context).apply {
            setContent {
                MaterialTheme {
                    Surface {
                        val focusManager = LocalFocusManager.current

                        SavedCardCvvScreen(
                            args = args,
                            onBackClick = {
                                focusManager.clearFocus()
                                navController.navigateUp()
                            },
                            onPlaceOrderClick = {
                                focusManager.clearFocus()
                                onPlaceOrderClick()
                            },
                            onCancelClick = {
                                focusManager.clearFocus()
                                navController.navigateUp()
                            },
                            isPlaceOrderEnabled = isPlaceOrderEnabled.value,
                            cvvView = cvvView
                        )
                    }
                }
            }
        }
    }

    private fun onPlaceOrderClick() {
        if (!isPlaceOrderEnabled.value) return

        val cardTokenizeOptions = getCardTokenizeOptions(
            (args.productForCheckout.totalRaw * 100).toInt(),
            args.savedCardChosen.paymentHandleTokenFrom,
            args.savedCardChosen.singleUseCustomerToken,
        )

        cardTokenizeOptionsAccountId = cardTokenizeOptions.accountId
        cardTokenizeOptionsMerchantRefNum = cardTokenizeOptions.merchantRefNum

        lifecycleScope.launchCatching(Dispatchers.IO) {
            val paymentHandleToken = cardController.tokenize(cardTokenizeOptions).value()
            withContext(Dispatchers.Main) {
                onPaymentResult(
                    SuccessDisplay(
                        accountId = cardTokenizeOptionsAccountId,
                        merchantReferenceNumber = cardTokenizeOptionsMerchantRefNum,
                        paymentHandleToken = paymentHandleToken
                    )
                )
            }
        }.onFailure {
            onPaymentError(it)
        }
    }

    private fun onPaymentResult(resultToDisplay: SuccessDisplay) {
        navController.navigate(
            FragmentSavedCardCvvDirections.actionSavedcvvToPaymentSuccessful(resultToDisplay)
        )
    }

    private fun onPaymentError(exception: Exception) {
        if (isAdded) {
            Log.e("SampleAppError", "FragmentSavedCardCvvCompose: $exception")
            ErrorHandlingDialog.newInstance(exception)
                .show(parentFragmentManager, ErrorHandlingDialog.TAG)
        } else {
            Log.w(
                "FragmentSavedCardCvvCompose",
                "Fragment is not attached, skipping error dialog."
            )
        }
    }

    private fun getCardTokenizeOptions(
        amount: Int,
        paymentHandleTokenFrom: String,
        singleUseCustomerToken: String,
    ) = PSCardTokenizeOptions(
        amount = amount,
        currencyCode = "USD",
        transactionType = TransactionType.PAYMENT,
        merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
        billingDetails = BillingDetails(
            nickName = "John Doe's card",
            street = "5335 Gate Parkway Fourth Floor",
            city = "Jacksonville",
            state = "FL",
            country = "US",
            zip = "32256"
        ),
        profile = Profile(
            firstName = "firstName",
            lastName = "lastName",
            locale = ProfileLocale.EN_GB,
            merchantCustomerId = "merchantCustomerId",
            dateOfBirth = DateOfBirth(1, 1, 1990),
            email = "email@mail.com",
            phone = "0123456789",
            mobile = "0123456789",
            gender = Gender.MALE,
            nationality = "nationality",
            identityDocuments = listOf(IdentityDocument(documentNumber = "SSN123456"))
        ),
        accountId = Consts.CARDS_ACCOUNT_ID,
        merchantDescriptor = MerchantDescriptor(
            dynamicDescriptor = "dynamicDescriptor",
            phone = "0123456789"
        ),
        shippingDetails = ShippingDetails(
            shipMethod = ShippingMethod.NEXT_DAY_OR_OVERNIGHT,
            street = "street",
            street2 = "street2",
            city = "Marbury",
            state = "AL",
            countryCode = "US",
            zip = "36051",
        ),
        singleUseCustomerToken = singleUseCustomerToken,
        paymentHandleTokenFrom = paymentHandleTokenFrom,
        renderType = RenderType.BOTH,
        threeDS = ThreeDS(
            merchantUrl = "https://api.qa.paysafe.com/checkout/v2/index.html#/desktop",
            process = true
        )
    )
}

