package com.example.fincalc.data.network.api_rates

class ResponseCurApi() {

    constructor(
        base: String,
        date: String,
        rates: Rates,
        success: Boolean,
        timestamp: Int
    ) : this() {
        this.base = base
        this.date = date
        this.rates = rates
        this.success = success
        this.timestamp = timestamp
    }


    var base: String = ""
    var date: String = ""
    lateinit var rates: Rates
    var success: Boolean = false
    var timestamp: Int = 0

}