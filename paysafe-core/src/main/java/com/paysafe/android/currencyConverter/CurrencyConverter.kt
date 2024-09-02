package com.paysafe.android.currencyConverter

import kotlin.math.pow

final class CurrencyConverter(
    private val multiplierMap: Map<PSCurrency, Int>
) {

    // Method to convert minor amount based on the currency.
    fun convert(amount: Int, forCurrency: String): Double {
        val psCurrency = PSCurrency.values().find { it.name == forCurrency }
        val power = psCurrency?.let { multiplierMap[it] } ?: 2
        if (psCurrency == null) {
            println("Currency doesn't need conversion: $forCurrency")
        }
        return amount.toDouble() / 10.0.pow(power.toDouble())
    }

    companion object {
        // Default multiplier map
        fun defaultCurrenciesMap(): Map<PSCurrency, Int> {
            return mapOf(
                PSCurrency.BHD to 3,
                PSCurrency.BYR to 0,
                PSCurrency.JPY to 0,
                PSCurrency.JOD to 3,
                PSCurrency.KRW to 0,
                PSCurrency.KWD to 3,
                PSCurrency.LYD to 3,
                PSCurrency.OMR to 3,
                PSCurrency.PYG to 0,
                PSCurrency.TND to 3,
                PSCurrency.VND to 0
            )
        }
    }
}