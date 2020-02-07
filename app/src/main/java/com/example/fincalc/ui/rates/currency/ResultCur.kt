package com.example.fincalc.ui.rates.currency

import com.example.fincalc.ui.rates.RatesBar
import com.example.fincalc.ui.rates.ResultToShow
import java.util.*

class ResultCur(
    override val ratesBarList: List<RatesBar>,
    override var baseCur: String,
    override var date: Date,
    var curFrom: String?,
    var resAmount: Double?
) : ResultToShow