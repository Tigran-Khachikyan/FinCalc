package com.example.fincalc.data.network.api_currency

import com.example.fincalc.data.network.Response

class ResponseCurrency : Response {

    override var date: String = ""
    override lateinit var rates: CurRates
    override var success: Boolean = false
    override var timestamp: Int = 0
    override var historical: Boolean = false
    override var base: String = ""
}