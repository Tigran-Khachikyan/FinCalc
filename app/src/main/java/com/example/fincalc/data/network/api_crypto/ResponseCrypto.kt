package com.example.fincalc.data.network.api_crypto

import com.google.gson.annotations.SerializedName

class ResponseCrypto {
    lateinit var rates: CryptoRates
    var success: Boolean = false
    var timestamp: Int = 0
    var historical: Boolean = false
    var date: String = ""
    @SerializedName("target")
    var base: String = ""
}