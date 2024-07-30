/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.select_payment_method

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.paysafe.android.PaysafeSDK
import com.paysafe.android.core.data.entity.PSCallback
import com.paysafe.android.core.domain.exception.PaysafeException
import com.paysafe.android.google_pay.PSGooglePayContext
import com.paysafe.android.google_pay.PSGooglePayTokenizeCallback
import com.paysafe.android.google_pay.button.PSGooglePayButtonOptions
import com.paysafe.android.google_pay.button.PSGooglePayPaymentMethodConfig
import com.paysafe.android.google_pay.domain.model.PSGooglePayConfig
import com.paysafe.android.google_pay.domain.model.PSGooglePayTokenizeOptions
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
import com.paysafe.android.tokenization.domain.model.paymentHandle.venmo.VenmoRequest
import com.paysafe.android.venmo.PSVenmoContext
import com.paysafe.android.venmo.PSVenmoTokenizeCallback
import com.paysafe.android.venmo.domain.model.PSVenmoConfig
import com.paysafe.android.venmo.domain.model.PSVenmoTokenizeOptions
import com.paysafe.example.R
import com.paysafe.example.databinding.FragmentSelectPaymentMethodBinding
import com.paysafe.example.successful.SuccessDisplay
import com.paysafe.example.util.Consts
import com.paysafe.example.util.ErrorHandlingDialog
import com.paysafe.example.util.longToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FragmentSelectPaymentMethod : Fragment() {

    private val args: FragmentSelectPaymentMethodArgs by navArgs()
    private val navController by lazy {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_sample_app) as NavHostFragment
        navHostFragment.navController
    }

    private lateinit var binding: FragmentSelectPaymentMethodBinding
    private var googlePayContext: PSGooglePayContext? = null
    private var venmoContext: PSVenmoContext? = null
    private var googlePayTokenizeOptionsAccountId = ""
    private var googlePayTokenizeOptionsMerchantRefNum = ""
    private var venmoTokenizeOptionsAccountId = ""
    private var venmoTokenizeOptionsMerchantRefNum = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PSGooglePayContext.initialize(
            this,
            PSGooglePayConfig(
                countryCode = "US",
                currencyCode = "USD",
                accountId = Consts.CARDS_ACCOUNT_ID,
                requestBillingAddress = true
            ),
            object : PSCallback<PSGooglePayContext> {
                override fun onSuccess(value: PSGooglePayContext) {
                    googlePayContext = value
                    initializeGooglePayButton(value.providePaymentMethodConfig())
                }

                override fun onFailure(exception: Exception) {
                    ErrorHandlingDialog.newInstance(
                        exception = exception,
                        title = "Google Pay init error"
                    ).show(
                        parentFragmentManager, ErrorHandlingDialog.TAG
                    )
                }

            }
        )

        PSVenmoContext.initialize(
            this@FragmentSelectPaymentMethod,
            providePSVenmoConfig(),
            object : PSCallback<PSVenmoContext> {
                override fun onSuccess(value: PSVenmoContext) {
                    venmoContext = value
                    showVenmoButton()
                }

                override fun onFailure(exception: Exception) {
                    logDebug("PSVenmoContext initialize error: $exception")
                    ErrorHandlingDialog.newInstance(
                        exception = exception,
                        title = "Venmo init error"
                    ).show(
                        parentFragmentManager, ErrorHandlingDialog.TAG
                    )
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectPaymentMethodBinding.inflate(inflater, container, false)

        binding.selectPayMethodBackImg.setOnClickListener {
            navController.navigateUp()
        }

        binding.selectPayMethodCreditCard.setOnClickListener {
            onCreditCardClick()
        }


        binding.selectPayMethodVenmo.setOnClickListener {
            onVenmoClick()
        }

        binding.selectPayMethodGooglePay.setOnClickListener {
            onGooglePayClick()
        }

        if (googlePayContext != null) {
            initializeGooglePayButton(googlePayContext!!.providePaymentMethodConfig())
        }

        if (venmoContext != null) {
            showVenmoButton()
        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        googlePayContext?.dispose()
    }

    internal fun initializeGooglePayButton(paymentMethodConfig: PSGooglePayPaymentMethodConfig) {
        val payButtonOptions = PSGooglePayButtonOptions.Builder(paymentMethodConfig).build()
        binding.selectPayMethodGooglePay.initialize(payButtonOptions)
    }

    internal fun showVenmoButton() {
        if (this::binding.isInitialized)
            binding.selectPayMethodVenmo.visibility = View.VISIBLE
    }

    internal fun onSuccessGooglePayment(paymentHandleToken: String) {
        val successDisplay = SuccessDisplay(
            accountId = googlePayTokenizeOptionsAccountId,
            merchantReferenceNumber = googlePayTokenizeOptionsMerchantRefNum,
            paymentHandleToken = paymentHandleToken
        )
        navController.navigate(
            FragmentSelectPaymentMethodDirections.actionNavSelectPaymentMethodToNavPaymentSuccessful(
                successDisplay
            )
        )
    }

    private fun onCreditCardClick() {
        navController.navigate(
            FragmentSelectPaymentMethodDirections.actionSelectPaymentMethodToSavedCard(
                args.productForCheckout
            )
        )
    }

    private fun onVenmoClick() {
        if (venmoContext == null) {
            context?.longToast("VenmoContext not initialized yet")
            return
        }

        val venmoTokenizeOptions = providePSVenmoTokenizeOptions()

        venmoTokenizeOptionsAccountId = venmoTokenizeOptions.accountId
        venmoTokenizeOptionsMerchantRefNum = venmoTokenizeOptions.merchantRefNum

        lifecycleScope.launch(Dispatchers.IO) {
            venmoContext?.tokenize(
                requireContext(),
                venmoTokenizeOptions,
                object : PSVenmoTokenizeCallback {

                    override fun onSuccess(paymentHandleToken: String) {
                        onVenmoPaymentSuccess(paymentHandleToken)
                    }

                    override fun onFailure(exception: Exception) {
                        logDebug("Venmo payment failed. Error message: ${exception.message}")
                        ErrorHandlingDialog.newInstance(exception).show(
                            parentFragmentManager, ErrorHandlingDialog.TAG
                        )
                    }

                    override fun onCancelled(paysafeException: PaysafeException) {
                        context?.longToast("Venmo payment was cancelled")
                    }
                }
            )
        }
    }

    private fun onGooglePayClick() {
        if (googlePayContext == null) {
            context?.longToast("GooglePayContext not initialized yet")
            return
        }

        val googlePayTokenizeOptions = providePSGooglePayTokenizeOptions()

        googlePayTokenizeOptionsAccountId = googlePayTokenizeOptions.accountId
        googlePayTokenizeOptionsMerchantRefNum = googlePayTokenizeOptions.merchantRefNum

        googlePayContext?.tokenize(
            googlePayTokenizeOptions,
            object : PSGooglePayTokenizeCallback {
                override fun onSuccess(paymentHandleToken: String) =
                    onSuccessGooglePayment(paymentHandleToken)

                override fun onFailure(paysafeException: PaysafeException) {
                    logDebug("Error: $paysafeException")
                    ErrorHandlingDialog.newInstance(paysafeException).show(
                        parentFragmentManager, ErrorHandlingDialog.TAG
                    )
                }

                override fun onCancelled(paysafeException: PaysafeException) {
                    context?.longToast("User Cancelled Google Pay Flow")
                }
            }
        )
    }

    private fun providePSVenmoConfig(): PSVenmoConfig = PSVenmoConfig(
        currencyCode = "USD",
        accountId = Consts.VENMO_ACCOUNT_ID
    )

    private fun providePSGooglePayTokenizeOptions(): PSGooglePayTokenizeOptions =
        PSGooglePayTokenizeOptions(
            amount = (args.productForCheckout.totalRaw * 100).toInt(),
            currencyCode = "USD",
            transactionType = TransactionType.PAYMENT,
            merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
            billingDetails = provideBillingDetails(),
            profile = provideProfile(),
            accountId = Consts.CARDS_ACCOUNT_ID,
            merchantDescriptor = provideMerchantDescriptor(),
            shippingDetails = provideShippingDetails(),
            threeDS = ThreeDS(
                merchantUrl = "https://api.qa.paysafe.com/checkout/v2/index.html#/desktop",
                process = true
            )
        )

    private fun providePSVenmoTokenizeOptions(): PSVenmoTokenizeOptions =
        PSVenmoTokenizeOptions(
            amount = (args.productForCheckout.totalRaw * 100).toInt(),
            currencyCode = "USD",
            transactionType = TransactionType.PAYMENT,
            merchantRefNum = PaysafeSDK.getMerchantReferenceNumber(),
            billingDetails = provideBillingDetails(),
            profile = provideProfile(),
            accountId = Consts.VENMO_ACCOUNT_ID,
            merchantDescriptor = provideMerchantDescriptor(),
            shippingDetails = provideShippingDetails(),
            customUrlScheme = "customScheme",
            venmoRequest = VenmoRequest(
                consumerId = "ala-bala-portocala",
                merchantAccountId = "",
                profileId = ""
                )
        )

    private fun provideBillingDetails() = BillingDetails(
        nickName = "nickName",
        street = "street",
        city = "city",
        state = "AL",
        country = "US",
        zip = "12345"
    )

    private fun provideProfile() = Profile(
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
    )

    private fun provideMerchantDescriptor() = MerchantDescriptor(
        dynamicDescriptor = "dynamicDescriptor",
        phone = "0123456789"
    )

    private fun provideShippingDetails() = ShippingDetails(
        shipMethod = ShippingMethod.NEXT_DAY_OR_OVERNIGHT,
        street = "street",
        street2 = "street2",
        city = "Marbury",
        state = "AL",
        countryCode = "US",
        zip = "36051",
    )


    internal fun onVenmoPaymentSuccess(paymentHandleToken: String) {
        navController.navigate(
            FragmentSelectPaymentMethodDirections.actionNavSelectPaymentMethodToNavPaymentSuccessful(
                SuccessDisplay(
                    accountId = venmoTokenizeOptionsAccountId,
                    merchantReferenceNumber = venmoTokenizeOptionsMerchantRefNum,
                    paymentHandleToken = paymentHandleToken
                )
            )
        )
    }

    internal fun logDebug(msg: String) = Log.d("FragmentSelectPaymentMethod", msg)
}