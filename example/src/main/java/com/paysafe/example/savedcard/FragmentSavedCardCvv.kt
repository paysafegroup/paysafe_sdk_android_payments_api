/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.savedcard

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import com.paysafe.android.hostedfields.domain.model.PSCardTokenizeOptions
import com.paysafe.android.tokenization.domain.model.paymentHandle.BillingDetails
import com.paysafe.android.tokenization.domain.model.paymentHandle.MerchantDescriptor
import com.paysafe.android.tokenization.domain.model.paymentHandle.RenderType
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
import com.paysafe.example.databinding.FragmentSavedCardCvvBinding
import com.paysafe.example.successful.SuccessDisplay
import com.paysafe.example.util.Consts
import com.paysafe.example.util.ErrorHandlingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FragmentSavedCardCvv : Fragment() {

    private val args: FragmentSavedCardCvvArgs by navArgs()
    private val navController by lazy {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_sample_app) as NavHostFragment
        navHostFragment.navController
    }

    private lateinit var binding: FragmentSavedCardCvvBinding
    private lateinit var cardController: PSCardFormController

    private var cardTokenizeOptionsAccountId = ""
    private var cardTokenizeOptionsMerchantRefNum = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedCardCvvBinding.inflate(inflater, container, false)
        setupUI(binding)
        PSCardFormController.initialize(
            cardFormConfig = PSCardFormConfig(
                currencyCode = "USD",
                accountId = Consts.CARDS_ACCOUNT_ID
            ),
            cardCvvView = binding.savedCardCvvField,
            callback = object : PSCallback<PSCardFormController> {
                override fun onSuccess(value: PSCardFormController) {
                    cardController = value
                }

                override fun onFailure(exception: Exception) {
                    ErrorHandlingDialog.newInstance(
                        exception = exception,
                        title = "CardForm init error"
                    ).show(
                        parentFragmentManager, ErrorHandlingDialog.TAG
                    )
                }

            }
        )

        return binding.root
    }

    private fun setupUI(binding: FragmentSavedCardCvvBinding) {
        binding.savedCardCvvField.cardType = args.savedCardChosen.cardBrandType
        binding.savedCardCvvBackImg.setOnClickListener {
            navController.navigateUp()
        }
        binding.savedCardCvvCCardBrand.setImageResource(args.savedCardChosen.cardBrandRes)
        binding.savedCardCvvLastDigits.text = "*" + args.savedCardChosen.lastDigits
        binding.savedCardCvvHolderName.text = args.savedCardChosen.holderName
        binding.savedCardCvvExpiryDate.text = args.savedCardChosen.expiryDate
        binding.savedCardCvvPlaceOrderButton.setOnClickListener {
            hideKeyboard(it)
            binding.savedCardCvvField.clearFocus()
            onPlaceOrderClick()
        }
        binding.savedCardCvvCancelButton.setOnClickListener {
            navController.navigateUp()
        }
        binding.savedCardCvvField.isValidLiveData.observe(viewLifecycleOwner) { isSubmitEnabled ->
            binding.savedCardCvvPlaceOrderButton.isEnabled = isSubmitEnabled
        }
    }

    private fun hideKeyboard(view: View) {
        (requireActivity().getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun onPlaceOrderClick() {
        binding.savedCardCvvPlaceOrderButton.text = "Loading..."
        binding.savedCardCvvPlaceOrderButton.isEnabled = false

        val cardTokenizeOptions = getCardTokenizeOptions(
            args.productForCheckout.totalRaw,
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
        binding.savedCardCvvPlaceOrderButton.text = "Place order"
        binding.savedCardCvvPlaceOrderButton.isEnabled = true
        navController.navigate(
            FragmentSavedCardCvvDirections.actionSavedcvvToPaymentSuccessful(resultToDisplay)
        )
    }

    private fun onPaymentError(it: Exception) {
        Log.e("SampleAppError", "FragmentSavedCardCvv: $it")
        binding.savedCardCvvPlaceOrderButton.text = "Place order"
        binding.savedCardCvvPlaceOrderButton.isEnabled = true
        ErrorHandlingDialog.newInstance(it).show(parentFragmentManager, ErrorHandlingDialog.TAG)
    }

    private fun getCardTokenizeOptions(
        amount: Int,
        paymentHandleTokenFrom: String,
        singleUseCustomerToken: String
    ) = PSCardTokenizeOptions(
        amount = amount,
        currencyCode = "USD",
        transactionType = TransactionType.PAYMENT,
        merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
        billingDetails = BillingDetails(
            nickName = "nickName",
            street = "street",
            city = "city",
            state = "AL",
            country = "US",
            zip = "12345"
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