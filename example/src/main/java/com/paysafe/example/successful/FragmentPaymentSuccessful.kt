/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.successful

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.paysafe.example.R
import com.paysafe.example.databinding.FragmentPaymentSuccessfulBinding

class FragmentPaymentSuccessful : Fragment() {

    private val args: FragmentPaymentSuccessfulArgs by navArgs()
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
        val binding = FragmentPaymentSuccessfulBinding.inflate(inflater, container, false)

        binding.paymentSuccessfulSentConfirmLabel.text = "Account id:"
        binding.paymentSuccessfulEmail.text = args.displayAtEnd.accountId ?: ""

        binding.paymentSuccessfulOrderNumberLabel.text = "Merchant reference number:"
        binding.paymentSuccessfulOrderNumber.text = args.displayAtEnd.merchantReferenceNumber ?: ""

        binding.paymentSuccessfulTicketDeliverLabel.text = "Payment handle token:"
        binding.paymentSuccessfulTicketDeliver.text = args.displayAtEnd.paymentHandleToken

        binding.paymentSuccessfulKeepShoppingButton.setOnClickListener {
            onKeepShoppingClick()
        }
        return binding.root
    }

    private fun onKeepShoppingClick() {
        navController.navigate(FragmentPaymentSuccessfulDirections.actionDismissScreen())
    }

}