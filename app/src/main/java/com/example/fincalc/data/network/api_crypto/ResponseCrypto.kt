package com.example.fincalc.data.network.api_crypto

import com.example.fincalc.data.network.Response

class ResponseCrypto : Response {
    override lateinit var rates: CryptoRates
    override var success: Boolean = false
    override var timestamp: Int = 0
    override var historical: Boolean = false
    override var date: String = ""
}