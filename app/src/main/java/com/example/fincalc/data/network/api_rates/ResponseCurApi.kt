package com.example.fincalc.data.network.api_rates

data class ResponseCurApi(
    val base: String,
    val date: String,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Int
)