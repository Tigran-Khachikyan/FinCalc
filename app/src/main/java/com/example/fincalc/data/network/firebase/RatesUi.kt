package com.example.fincalc.data.network.firebase

import com.example.fincalc.data.network.api_rates.Rates
import java.util.*

data class RatesUi(
    val dateTime: Date?,
    val latRates: Rates?,
    val elderRates: Rates?,
    var status: Int = OK
) {
    override fun toString(): String {
        return "dateTime: $dateTime, LATEST-AMD: ${latRates?.AMD}," +
                " ELDER-AMD: ${elderRates?.AMD}, STATUS: $status"
    }
}