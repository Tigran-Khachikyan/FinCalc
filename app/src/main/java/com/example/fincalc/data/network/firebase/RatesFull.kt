package com.example.fincalc.data.network.firebase

import com.example.fincalc.data.network.Rates
import java.util.*

data class RatesFull(
    val dateTime: Date?,
    val latRates: Rates?,
    val elderRates: Rates?,
    val base: String?,
    var status: Int = OK
)