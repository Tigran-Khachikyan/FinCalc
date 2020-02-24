package com.my_1st.fincalc.ui.rates.metals

import com.my_1st.fincalc.ui.rates.RatesBar
import com.my_1st.fincalc.ui.rates.ResultToShow
import java.util.*

class ResultMet(
    override val ratesBarList: List<RatesBar>,
    override var baseCur: String,
    override var date: Date,
    override var status: Int,
    var unit: MetalsUnit
) : ResultToShow