package com.my_1st.fincalc.models.rates

import kotlin.math.absoluteValue

fun getGrowthRate(latValue: Double?, oldValue: Double?): Float? {
    return if (latValue != null && oldValue != null) {
        val dif = latValue - oldValue
        val res = (100 * dif / oldValue).toFloat()
        when {
            res == 0F || res.absoluteValue < 0.0001 -> 0F
            res.absoluteValue > 0.0001 && res.absoluteValue < 0.001 -> if (res > 0.0) 0.001F else -0.001F
            else -> res
        }
    } else null
}