package com.example.fincalc.data.network.api_metals

data class ResponseMetalApi(
    val base: String,
    val date: String,
    val rates: RatesMetal,
    val success: Boolean,
    val timestamp: Int,
    val unit: String
)