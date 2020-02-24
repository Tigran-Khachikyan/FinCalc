package com.my_1st.fincalc.ui.rates.crypto

import com.my_1st.fincalc.ui.rates.RatesBar

class RatesBarCrypto(
    override val code: String,
    override val name: String,
    override val icon: Int,
    override var price: Double,
    override var growth: Float?,
    val pop: Int
) : RatesBar(
    code,
    name,
    icon,
    price,
    growth
)