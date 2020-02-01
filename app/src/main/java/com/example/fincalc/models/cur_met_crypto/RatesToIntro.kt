package com.example.fincalc.models.cur_met_crypto

import com.example.fincalc.data.network.api_rates.RatesCurrency
import com.example.fincalc.data.network.firebase.RatesFull

class TableRates(val baseCurrency: String, val rates: RatesFull)

class ConvertRates(val resultAmount: Double?, val rates: RatesCurrency)