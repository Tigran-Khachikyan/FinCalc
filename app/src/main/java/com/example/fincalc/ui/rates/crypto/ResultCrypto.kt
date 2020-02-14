package com.example.fincalc.ui.rates.crypto

import com.example.fincalc.ui.rates.RatesBar
import com.example.fincalc.ui.rates.ResultToShow
import java.util.*

class ResultCrypto(
    override val ratesBarList: List<RatesBarCrypto>,
    override var baseCur: String,
    override var date: Date,
    var order: Order
) : ResultToShow