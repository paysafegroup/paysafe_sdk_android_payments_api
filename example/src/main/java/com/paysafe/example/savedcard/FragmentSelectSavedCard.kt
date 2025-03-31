/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.savedcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.paysafe.example.R
import com.paysafe.example.databinding.FragmentSelectSavedCardBinding
import com.paysafe.example.util.ErrorHandlingDialog
import com.paysafe.example.util.dpToPx
import kotlinx.coroutines.launch

class FragmentSelectSavedCard : Fragment() {

    private val args: FragmentSelectSavedCardArgs by navArgs()
    private val navController by lazy {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_sample_app) as NavHostFragment
        navHostFragment.navController
    }

    private lateinit var binding: FragmentSelectSavedCardBinding

    private val viewModel by viewModels<SavedCardsViewModel>()
    private val savedCardsAdapter by lazy {
        SavedCardsAdapter(::onSavedCardClick)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSelectSavedCardBinding.inflate(inflater, container, false)
        val savedCardsRecyclerView: RecyclerView = binding.recyclerSavedCardsList

        savedCardsRecyclerView.adapter = savedCardsAdapter
        savedCardsRecyclerView.addItemDecoration(listDivider())
        savedCardsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.selectSavedCardBackImg.setOnClickListener {
            navController.navigateUp()
        }
        binding.selectSavedCardAddNewCard.setOnClickListener {
            onAddNewCardClick()
        }
        binding.selectSavedCardAddNewCardCompose.setOnClickListener {
            onAddNewCardComposeClick()
        }

        viewModel.onRequestSingleUseCustomerTokens()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSavedCardsData()
    }

    private fun listDivider() = MaterialDividerItemDecoration(
        requireContext(), LinearLayoutManager.VERTICAL
    ).apply {
        dividerThickness = requireContext().dpToPx(16).toInt()
        setDividerColorResource(requireContext(), R.color.white)
    }

    private fun onAddNewCardClick() {
        navController.navigate(
            FragmentSelectSavedCardDirections.actionAddNewCreditCard(args.productForCheckout)
        )
    }

    private fun onAddNewCardComposeClick() {
        navController.navigate(
            FragmentSelectSavedCardDirections.actionAddNewCreditCardCompose(args.productForCheckout)
        )
    }

    private fun onSavedCardClick(selectedSavedCard: UiSavedCardData) {
        if (selectedSavedCard.holderName == "Compose saved card") {
            navController.navigate(
                FragmentSelectSavedCardDirections.actionSelectSavedCardCompose(
                    args.productForCheckout, selectedSavedCard
                )
            )
        } else {
            navController.navigate(
                FragmentSelectSavedCardDirections.actionSelectSavedCard(
                    args.productForCheckout, selectedSavedCard
                )
            )
        }
    }

    private fun observeSavedCardsData() {
        lifecycleScope.launch {
            viewModel.savedCardsLiveData.collect { state ->
                when (state) {
                    is SavedCardsUiState.SUCCESS -> {
                        binding.savedCardsProgressBar.isVisible = false
                        savedCardsAdapter.updateData(state.data)
                    }

                    is SavedCardsUiState.LOADING -> binding.savedCardsProgressBar.isVisible = true
                    is SavedCardsUiState.FAILURE -> {
                        binding.savedCardsProgressBar.isVisible = false
                        ErrorHandlingDialog.newInstance(state.exception).show(
                            parentFragmentManager, ErrorHandlingDialog.TAG
                        )
                    }
                }
            }
        }
    }
}