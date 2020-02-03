package com.example.fincalc.models.rates

import com.example.fincalc.data.network.api_currency.CurRates
import com.example.fincalc.data.network.firebase.RatesFull

class TableRates(val baseCurrency: String, val rates: RatesFull)

class CurrencyConverter(val resultAmount: Double?, val rates: CurRates)
