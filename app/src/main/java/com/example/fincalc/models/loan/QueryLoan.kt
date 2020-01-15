package com.example.fincalc.models.loan


data class QueryLoan(
    val sum: Long,
    val months: Int,
    val rate: Float,
    val oneTimeCommissionSum: Double = 0.0,
    val oneTimeOtherCosts: Double = 0.0,
    val annualCommissionSum: Double = 0.0,
    val annualCommissionRate: Float = 0F,
    val oneTimeCommissionRate: Float = 0F,
    val monthlyCommissionSum: Double = 0.0,
    val monthlyCommissionRate: Float = 0F,
    val minOneTimeCommissionSumOrRate: Boolean = false,
    val minMonthlyCommissionSumOrRate: Boolean = false,
    val minAnnualCommissionSumOrRate: Boolean = false
)