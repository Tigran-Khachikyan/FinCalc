package com.example.fincalc.data.network.firebase

import com.example.fincalc.data.network.api_rates.RatesCurrency
import java.util.*

data class RatesFull(
    val dateTime: Date?,
    val latRates: RatesCurrency?,
    val elderRates: RatesCurrency?,
    var status: Int = OK
) {
    override fun toString(): String {
        return "dateTime: $dateTime, LATEST-AMD: ${latRates?.AMD}," +
                " ELDER-AMD: ${elderRates?.AMD}, STATUS: $status"
    }
}