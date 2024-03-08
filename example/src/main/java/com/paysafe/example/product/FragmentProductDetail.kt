/*
 * Copyright (c) 2024 Paysafe Group
 */

package com.paysafe.example.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paysafe.example.R
import com.paysafe.example.databinding.FragmentProductDetailBinding

class FragmentProductDetail : Fragment() {

    private var currentSelectedQtyIndex = 0
    private val args: FragmentProductDetailArgs by navArgs()
    private lateinit var productQtyAdapter: QuantitiesAdapter

    private val navController by lazy {
        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_sample_app) as NavHostFragment
        navHostFragment.navController
    }

    private var quantitiesIds = intArrayOf(
        R.id.quantityOne,
        R.id.quantityTwo,
        R.id.quantityThree,
        R.id.quantityFour,
        R.id.quantityFive,
        R.id.quantitySix,
        R.id.quantitySeven,
        R.id.quantityEight,
        R.id.quantityNine,
        R.id.quantityTen
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        fillDetailWithData(binding)
        return binding.root
    }

    private fun fillDetailWithData(binding: FragmentProductDetailBinding) {
        binding.productDetailCloseImg.setOnClickListener {
            navController.navigateUp()
        }
        binding.productDetailImg.setImageResource(args.selectedProduct.imageRes)
        if (args.selectedProduct.isNew) {
            binding.productDetailIsNew.visibility = View.VISIBLE
        }
        binding.productDetailNameText.text = args.selectedProduct.name
        if (args.selectedProduct.isFavorite) {
            binding.productDetailFavoriteImg.setImageResource(R.drawable.ic_fav_filled)
        } else {
            binding.productDetailFavoriteImg.setImageResource(R.drawable.ic_fav_outlined)
        }
        binding.productDetailPriceText.text = args.selectedProduct.price
        binding.productDateText.text = args.selectedProduct.date

        createQuantityPicker(binding)

        if (args.selectedProduct.description.isEmpty()) {
            binding.productDetailDescriptionLabel.visibility = View.GONE
            binding.productDetailDescription.visibility = View.GONE
        } else {
            binding.productDetailDescription.text = args.selectedProduct.description
        }
        binding.buyItNow.setOnClickListener {
            onBuyItNowClick()
        }
    }

    private fun createQuantityPicker(binding: FragmentProductDetailBinding) {
        val productQtyRecyclerView: RecyclerView = binding.recyclerProductQuantities
        productQtyAdapter = QuantitiesAdapter(
            quantitiesDataToDisplay(args.selectedProduct.quantity)
        )
        productQtyRecyclerView.adapter = productQtyAdapter
        productQtyRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
    }

    private fun quantitiesDataToDisplay(initWithQty: String): MutableList<UiProductQtyData> {
        val quantitiesList = mutableListOf<UiProductQtyData>()
        for (qty in 1..10) {
            quantitiesList.add(
                UiProductQtyData(
                    quantitiesIds[qty - 1],
                    qty.toString(),
                    initWithQty == qty.toString(),
                    ::onQuantityClick
                )
            )
        }
        currentSelectedQtyIndex = initWithQty.toInt() - 1
        return quantitiesList
    }

    private fun onQuantityClick(newSelectedIndex: Int, newQuantity: String) {
        if (currentSelectedQtyIndex == newSelectedIndex) return
        productQtyAdapter.selectQuantity(newSelectedIndex)
        productQtyAdapter.unselectQuantity(currentSelectedQtyIndex)
        currentSelectedQtyIndex = newSelectedIndex

        args.selectedProduct.quantity = newQuantity
        args.selectedProduct.totalToDisplay = recalculateTotal(
            args.selectedProduct.price, newQuantity.toInt()
        )
        args.selectedProduct.totalRaw = args.selectedProduct.totalToDisplay
            .replace("$", "").toDouble()
    }

    private fun recalculateTotal(
        olderPrice: String,
        quantity: Int
    ) = "$%.2f".format(olderPrice.replace("$", "").toDouble() * quantity)

    private fun onBuyItNowClick() {
        navController.navigate(
            FragmentProductDetailDirections.actionProductToCheckout(args.selectedProduct)
        )
    }

}