package com.example.fincalc.data.network.api_crypto

import com.google.gson.annotations.SerializedName

class ResponseCrypto {
    lateinit var rates: CryptoRates
    var date: String = ""
    @SerializedName("target")
    var base: String = ""
}