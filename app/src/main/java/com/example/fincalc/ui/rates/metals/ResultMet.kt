package com.example.fincalc.ui.rates.metals

import com.example.fincalc.ui.rates.RatesBar
import com.example.fincalc.ui.rates.ResultToShow
import java.util.*

class ResultMet(
    override val ratesBarList: List<RatesBar>,
    override var baseCur: String,
    override var date: Date,
    var isUnitOunce: Boolean
) : ResultToShow