package com.example.fincalc.data.network.api_rates

import com.example.fincalc.data.network.Response

class ResponseCurrency : Response {

    /* constructor(
         base: String,
         date: String?,
         rates: RatesCurrency,
         success: Boolean,
         timestamp: Int,
         historical: Boolean?
     ) : this() {
         this.base = base
         this.date = date
         this.rates = rates
         this.success = success
         this.timestamp = timestamp
         this.historical = historical
     }*/

    override var date: String = ""
    override lateinit var rates: CurRates
    override var success: Boolean = false
    override var timestamp: Int = 0
    override var historical: Boolean = false

}