package com.my_1st.fincalc.data.network.firebase

import com.my_1st.fincalc.data.network.Rates
import java.util.*

data class RatesFull(
    val dateTime: Date?,
    val latRates: Rates?,
    val elderRates: Rates?,
    val base: String?,
    var status: Int = OK
)