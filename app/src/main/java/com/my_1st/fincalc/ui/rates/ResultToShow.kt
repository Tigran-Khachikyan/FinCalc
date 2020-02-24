package com.my_1st.fincalc.ui.rates

import java.util.*

interface ResultToShow {
    val ratesBarList: List<RatesBar>
    val baseCur: String
    val date: Date
    val status: Int
}