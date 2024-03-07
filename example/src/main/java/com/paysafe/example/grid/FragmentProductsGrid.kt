/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.grid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.paysafe.example.R
import com.paysafe.example.databinding.FragmentProductsGridBinding

class FragmentProductsGrid : Fragment() {

    private val productsAdapter = ProductsAdapter(productsDataToDisplay())

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
        val binding = FragmentProductsGridBinding.inflate(inflater, container, false)
        val productsRecyclerView: RecyclerView = binding.recyclerProductsGrid

        productsRecyclerView.adapter = productsAdapter
        productsRecyclerView.addItemDecoration(xGridDivider())
        productsRecyclerView.addItemDecoration(yGridDivider())
        productsRecyclerView.layoutManager = GridLayoutManager(
            requireContext(), resources.getInteger(R.integer.products_grid_columns)
        )
        return binding.root
    }

    private fun xGridDivider() = MaterialDividerItemDecoration(
        requireContext(), GridLayoutManager.HORIZONTAL
    ).apply {
        isLastItemDecorated = false
        setDividerColorResource(requireContext(), R.color.white)
    }

    private fun yGridDivider() = MaterialDividerItemDecoration(
        requireContext(), GridLayoutManager.VERTICAL
    ).apply {
        isLastItemDecorated = false
        setDividerColorResource(requireContext(), R.color.white)
    }

    private fun productsDataToDisplay() = mutableListOf(
        UiProductData(
            R.id.drawGames,
            true,
            R.drawable.ic_calendar,
            true,
            "$0.99",
            "Draw Games",
            "Aug 28, 2023 - 3PM",
            "1",
            99,
            "$0.99",
            "Draw Games offer an exhilarating opportunity to test your luck and win big by selecting a set of numbers",
            ::onProductClick
        ),
        UiProductData(
            R.id.scratchOff,
            false,
            R.drawable.ic_cheque,
            true,
            "$0.99",
            "Scratch Off",
            "Aug 28, 2023 - 3PM",
            "1",
            99,
            "$0.99",
            "",
            ::onProductClick
        ),
        UiProductData(
            R.id.instantWin,
            false,
            R.drawable.ic_bank_note,
            false,
            "$0.99",
            "Instant Win",
            "Aug 28, 2023 - 3PM",
            "1",
            99,
            "$0.99",
            "",
            ::onProductClick
        ),
        UiProductData(
            R.id.raffles,
            false,
            R.drawable.ic_car,
            false,
            "$0.99",
            "Raffles",
            "Aug 28, 2023 - 3PM",
            "1",
            99,
            "$0.99",
            "",
            ::onProductClick
        ),
        UiProductData(
            R.id.luckyWheel,
            false,
            R.drawable.ic_wheel_of_fortune,
            false,
            "$0.99",
            "Lucky Wheel",
            "Aug 28, 2023 - 3PM",
            "1",
            99,
            "$0.99",
            "",
            ::onProductClick
        ),
        UiProductData(
            R.id.treasureHunt,
            false,
            R.drawable.ic_crown,
            false,
            "$0.99",
            "Treasure Hunt",
            "Aug 28, 2023 - 3PM",
            "1",
            99,
            "$0.99",
            "",
            ::onProductClick
        )
    )

    private fun onProductClick(index: Int) {
        navController.navigate(
            FragmentProductsGridDirections.actionProductsToDetail(productsAdapter.getProduct(index))
        )
    }
}