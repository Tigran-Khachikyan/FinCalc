package com.example.fincalc.data.network.api_cur_metal

data class Response(
    val base: String,
    val date: String,
    val rates: CurMetRates
)