package com.my_1st.fincalc.ui.rates.crypto

import com.my_1st.fincalc.ui.rates.ResultToShow
import java.util.*

class ResultCrypto(
    override val ratesBarList: List<RatesBarCrypto>,
    override var baseCur: String,
    override var date: Date,
    override val status: Int,
    var order: Order
) : ResultToShow