package com.example.fincalc.data.network.firebase

import com.example.fincalc.data.network.Rates
import java.util.*

data class RatesFull(
    val dateTime: Date?,
    val latRates: Rates?,
    val elderRates: Rates?,
    var status: Int = OK
) {
    override fun toString(): String {
        return "dateTime: $dateTime, LATEST-AMD: ${latRates}," +
                " ELDER-AMD: ${elderRates}, STATUS: $status"
    }
}