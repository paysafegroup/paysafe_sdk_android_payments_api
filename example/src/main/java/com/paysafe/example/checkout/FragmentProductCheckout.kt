/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paysafe.example.R
import com.paysafe.example.databinding.FragmentProductCheckoutBinding

class FragmentProductCheckout : BottomSheetDialogFragment() {

    private val args: FragmentProductCheckoutArgs by navArgs()
    private val navController by lazy {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_sample_app) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setOnShowListener {
            (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val binding = FragmentProductCheckoutBinding.inflate(inflater, container, false)
        isCancelable = false
        fillCheckoutWithData(binding)
        return binding.root
    }

    private fun fillCheckoutWithData(binding: FragmentProductCheckoutBinding) {
        binding.close.setOnClickListener {
            navController.navigateUp()
        }
        binding.checkoutProductImg.setImageResource(args.productForCheckout.imageRes)
        binding.checkoutProductNameText.text = args.productForCheckout.name
        binding.checkoutProductDateText.text = args.productForCheckout.date
        binding.selectPaymentMethod.setOnClickListener {
            onSelectPaymentMethodClick()
        }
        binding.billingAddressText.text = BillingAddress(
            "John",
            "Doe",
            "5335 Gate Parkway Fourth Floor",
            "Jacksonvillle",
            "FL",
            "32256"
        ).formattedAddress()
        binding.checkoutSelectBillingAddress.setOnClickListener {}
        binding.checkoutPromoCode.setOnClickListener {}
        binding.checkoutTotalValueText.text = args.productForCheckout.totalToDisplay
        binding.checkoutTotal.setOnClickListener {}
        binding.checkoutTermsAndPrivacyTwoLabel.setOnClickListener {}
        binding.checkoutPlaceOrderButton.setOnClickListener {
            onPlaceOrderClick()
        }
        binding.checkoutCancelOrderButton.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun onSelectPaymentMethodClick() {
        navController.navigate(
            FragmentProductCheckoutDirections.actionCheckoutToSelectPaymentMethod(
                args.productForCheckout
            )
        )
    }

    private fun onPlaceOrderClick() {
        // NOOP
    }

}