package com.example.fincalc.data.network.api_metals

data class ResponseMetals(
    val base: String,
    val date: String,
    val rates: MetalRates,
    val success: Boolean,
    val timestamp: Int,
    val unit: String
)