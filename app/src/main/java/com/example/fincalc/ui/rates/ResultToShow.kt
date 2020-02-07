package com.example.fincalc.ui.rates

import java.util.*

interface ResultToShow {
    val ratesBarList: List<RatesBar>
    val baseCur: String
    val date: Date
}