/*
 * Copyright (c) 2025 Paysafe Group
 */

package com.paysafe.example.creditcard

import PSCardForm
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.data.entity.value
import com.paysafe.android.core.util.launchCatching
import com.paysafe.android.hostedfields.PSCardFormConfig
import com.paysafe.android.hostedfields.PSCardFormController
import com.paysafe.android.hostedfields.cardnumber.PSCardNumberView
import com.paysafe.android.hostedfields.cvv.PSCvvView
import com.paysafe.android.hostedfields.domain.model.PSCardTokenizeOptions
import com.paysafe.android.hostedfields.domain.model.RenderType
import com.paysafe.android.hostedfields.expirydate.PSExpiryDatePickerView
import com.paysafe.android.hostedfields.holdername.PSCardholderNameView
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
import kotlinx.coroutines.withContext

class FragmentCreditCardCompose: Fragment() {

    private var cardController: PSCardFormController? = null

    private val args: FragmentCreditCardComposeArgs by navArgs()
    private var cardTokenizeOptionsAccountId = ""
    private var cardTokenizeOptionsMerchantRefNum = ""
    private val navController by lazy {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_sample_app) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        val context = requireContext()

        val cardNumberView = PSCardNumberView(context)
        val cardHolderNameView = PSCardholderNameView(context)
        val expiryDateView = PSExpiryDatePickerView(context)
        val cvvView = PSCvvView(context)
        val isSubmitEnabled = mutableStateOf(false)

        PSCardFormController.initialize(
            cardFormConfig = PSCardFormConfig("USD", "1001234110"),
            cardNumberView = cardNumberView,
            cardHolderNameView = cardHolderNameView,
            cardExpiryDateView = expiryDateView,
            cardCvvView = cvvView,
            callback = object : PSCallback<PSCardFormController> {
                override fun onSuccess(value: PSCardFormController) {
                    cardController = value
                    cardController?.isSubmitEnabledLiveData?.observe(viewLifecycleOwner) { isEnabled ->
                        isSubmitEnabled.value = isEnabled
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
                            "FragmentCreditCardCompose",
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            val focusManager = LocalFocusManager.current
                            PSCardForm(
                                isSubmitEnabled = isSubmitEnabled.value,
                                onSubmit = {
                                    focusManager.clearFocus()
                                    processPayment()
                                },
                                onCancel = {
                                    focusManager.clearFocus()
                                    navController.navigateUp()
                                },
                                remember { cardNumberView },
                                remember { cardHolderNameView },
                                remember { expiryDateView },
                                remember { cvvView },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cardController?.dispose()
    }

    private fun processPayment() {
        view?.let { hideKeyboard(it) }

        val cardTokenizeOptions = getCardTokenizeOptions(
            (args.productToUseCreditCard.totalRaw * 100).toInt()
        )

        cardTokenizeOptionsAccountId = cardTokenizeOptions.accountId
        cardTokenizeOptionsMerchantRefNum = cardTokenizeOptions.merchantRefNum

        lifecycleScope.launchCatching(Dispatchers.IO) {
            val paymentHandleToken = cardController?.tokenize(cardTokenizeOptions)?.value()
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
            FragmentCreditCardComposeDirections.actionNewccToPaymentSuccessful(resultToDisplay)
        )
    }

    private fun onPaymentError(it: Exception) {
        if (isAdded) {
            ErrorHandlingDialog.newInstance(it).show(parentFragmentManager, ErrorHandlingDialog.TAG)
        } else {
            Log.w(
                "FragmentCreditCardCompose",
                "Fragment is not attached, skipping error dialog."
            )
        }
    }

    private fun getCardTokenizeOptions(amount: Int) = PSCardTokenizeOptions(
        amount = amount,
        currencyCode = "USD",
        transactionType = TransactionType.PAYMENT,
        merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
        billingDetails = BillingDetails(
            nickName = "John Doe's card",
            street = "5335 Gate Parkway Fourth Floor",
            city = "Jacksonvillle",
            state = "FL",
            country = "US",
            zip = "32256"
        ),
        profile = Profile(
            firstName = "firstName",
            lastName = "lastName",
            locale = ProfileLocale.EN_GB,
            merchantCustomerId = "merchantCustomerId",
            dateOfBirth = DateOfBirth(
                day = 1,
                month = 1,
                year = 1990
            ),
            email = "email@mail.com",
            phone = "0123456789",
            mobile = "0123456789",
            gender = Gender.FEMALE,
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
        renderType = RenderType.BOTH,
        threeDS = ThreeDS(
            merchantUrl = "https://api.qa.paysafe.com/checkout/v2/index.html#/desktop",
            process = true
        )
    )
}