/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.paysafe.example.R
import com.paysafe.example.databinding.FragmentProductCategoriesBinding

class FragmentProductCategories : Fragment() {

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
        val binding = FragmentProductCategoriesBinding.inflate(inflater, container, false)
        val categoriesRecyclerView: RecyclerView = binding.recyclerProductCategoriesList
        val productCategoriesAdapter = ProductCategoriesAdapter(categoriesDataToDisplay())

        categoriesRecyclerView.adapter = productCategoriesAdapter
        categoriesRecyclerView.addItemDecoration(categoriesDivider())
        categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    private fun categoriesDivider() = MaterialDividerItemDecoration(
        requireContext(), LinearLayoutManager.VERTICAL
    ).apply {
        isLastItemDecorated = false
        setDividerColorResource(requireContext(), R.color.white)
    }

    private fun categoriesDataToDisplay() = mutableListOf(
        UiProductCategoryData(
            R.id.lotteryTickets,
            getString(R.string.lottery_tickets),
            R.drawable.lottery_tickets_woman,
            ::onCategoryClick
        ),
        UiProductCategoryData(
            R.id.raffleDraws,
            getString(R.string.raffle_draws),
            R.drawable.raffle_draws_woman,
            ::onCategoryClick
        ),
        UiProductCategoryData(
            R.id.bingoGames,
            getString(R.string.bingo_games),
            R.drawable.bingo_games_man,
            ::onCategoryClick
        ),
        UiProductCategoryData(
            R.id.triviaAndQuizes,
            getString(R.string.trivia_quizes),
            R.drawable.trivia_quizes_man,
            ::onCategoryClick
        )
    )

    private fun onCategoryClick() {
        navController.navigate(R.id.action_categories_to_products_grid)
    }

}