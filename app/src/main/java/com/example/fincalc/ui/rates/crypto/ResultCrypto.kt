package com.example.fincalc.ui.rates.crypto

import com.example.fincalc.ui.rates.ResultToShow
import java.util.*

class ResultCrypto(
    override val ratesBarList: List<RatesBarCrypto>,
    override var baseCur: String,
    override var date: Date,
    override val status: Int,
    var order: Order
) : ResultToShow