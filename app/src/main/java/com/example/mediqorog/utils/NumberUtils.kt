package com.example.mediqorog.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

object NumberUtils {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "NP"))
    private val decimalFormat = DecimalFormat("#,##0.00")
    private val integerFormat = DecimalFormat("#,##0")

    fun formatCurrency(amount: Double, symbol: String = "₹"): String {
        return try {
            "$symbol${integerFormat.format(amount)}"
        } catch (e: Exception) {
            "$symbol 0"
        }
    }

    fun formatCurrencyWithDecimals(amount: Double, symbol: String = "₹"): String {
        return try {
            "$symbol${decimalFormat.format(amount)}"
        } catch (e: Exception) {
            "$symbol 0.00"
        }
    }

    fun formatNumber(number: Int): String {
        return try {
            integerFormat.format(number)
        } catch (e: Exception) {
            "0"
        }
    }

    fun formatNumber(number: Double): String {
        return try {
            decimalFormat.format(number)
        } catch (e: Exception) {
            "0.00"
        }
    }

    fun formatPercentage(value: Double, decimals: Int = 1): String {
        return try {
            val format = when (decimals) {
                0 -> DecimalFormat("#0")
                1 -> DecimalFormat("#0.0")
                2 -> DecimalFormat("#0.00")
                else -> DecimalFormat("#0.0")
            }
            "${format.format(value)}%"
        } catch (e: Exception) {
            "0%"
        }
    }

    fun formatCompactNumber(number: Int): String {
        return when {
            number >= 1000000 -> "${(number / 1000000.0).format(1)}M"
            number >= 1000 -> "${(number / 1000.0).format(1)}K"
            else -> number.toString()
        }
    }

    fun formatCompactCurrency(amount: Double, symbol: String = "₹"): String {
        return when {
            amount >= 10000000 -> "$symbol${(amount / 10000000.0).format(1)}Cr"
            amount >= 100000 -> "$symbol${(amount / 100000.0).format(1)}L"
            amount >= 1000 -> "$symbol${(amount / 1000.0).format(1)}K"
            else -> formatCurrency(amount, symbol)
        }
    }

    private fun Double.format(decimals: Int): String {
        return "%.${decimals}f".format(this)
    }

    fun parseDouble(value: String): Double {
        return try {
            value.replace(",", "").toDouble()
        } catch (e: Exception) {
            0.0
        }
    }

    fun parseInt(value: String): Int {
        return try {
            value.replace(",", "").toInt()
        } catch (e: Exception) {
            0
        }
    }
}