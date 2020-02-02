package com.example.fincalc.models.rates

import com.example.fincalc.data.network.api_crypto.CryptoRates

val cryptoMap = sortedMapOf(
    "BTC" to "itcoin",
    "ETH" to "Ethereum",
    "XRP" to "XRP",
    "BCH" to "itcoin Cash",
    "USDT" to "Tether",
    "LTC" to "Litecoin",
    "EOS" to "EOS",
    "BNB" to "Binance Coin",
    "ADA" to "Cardano",
    "ETC" to "Ethereum Classic",
    "XMR" to "Monero",
    "TRX" to "TRON",
    "XLM" to "Stellar",
    "XTZ" to "Tezos",
    "DASH" to "Dash",
    "LINK" to "Chainlink",
    "LEO" to "UNUS SED LEO",
    "MIOTA" to "IOTA",
    "NEO" to "Neo",
    "ZEC" to "Zcash",
    "MKR" to "Maker",
    "XEM" to "NEM",
    "BAT" to "Basic Attention Token",
    "DOGE" to "Dogecoin",
    "QTUM" to "Qtum"
)

fun getMapFromCryptoRates(rates: CryptoRates): HashMap<String, Double>? = hashMapOf(
    "BTC" to rates.BTC,
    "ETH" to rates.ETH,
    "XRP" to rates.XRP,
    "BCH" to rates.BCH,
    "USDT" to rates.USDT,
    "LTC" to rates.LTC,
    "EOS" to rates.EOS,
    "BNB" to rates.BNB,
    "ADA" to rates.ADA,
    "ETC" to rates.ETC,
    "XMR" to rates.XMR,
    "TRX" to rates.TRX,
    "XLM" to rates.XLM,
    "XTZ" to rates.XTZ,
    "DASH" to rates.DASH,
    "LINK" to rates.LINK,
    "LEO" to rates.LEO,
    "MIOTA" to rates.MIOTA,
    "NEO" to rates.NEO,
    "ZEC" to rates.ZEC,
    "MKR" to rates.MKR,
    "XEM" to rates.XEM,
    "BAT" to rates.BAT,
    "DOGE" to rates.DOGE,
    "QTUM" to rates.QTUM
)

fun getCryptoRatesFromMap(map: HashMap<String, Double>): CryptoRates {
    val rates = CryptoRates()

    rates.BTC = map["BTC"] ?: 0.0
    rates.ETH = map["ETH"] ?: 0.0
    rates.XRP = map["XRP"] ?: 0.0
    rates.BCH = map["BCH"] ?: 0.0
    rates.USDT = map["USDT"] ?: 0.0
    rates.LTC = map["LTC"] ?: 0.0
    rates.EOS = map["EOS"] ?: 0.0
    rates.BNB = map["BNB"] ?: 0.0
    rates.ADA = map["ADA"] ?: 0.0
    rates.ETC = map["ETC"] ?: 0.0
    rates.XMR = map["XMR"] ?: 0.0
    rates.TRX = map["TRX"] ?: 0.0
    rates.XLM = map["XLM"] ?: 0.0
    rates.XTZ = map["XTZ"] ?: 0.0
    rates.DASH = map["DASH"] ?: 0.0
    rates.LINK = map["LINK"] ?: 0.0
    rates.LEO = map["LEO"] ?: 0.0
    rates.MIOTA = map["MIOTA"] ?: 0.0
    rates.NEO = map["NEO"] ?: 0.0
    rates.ZEC = map["ZEC"] ?: 0.0
    rates.MKR = map["MKR"] ?: 0.0
    rates.XEM = map["XEM"] ?: 0.0
    rates.BAT = map["BAT"] ?: 0.0
    rates.DOGE = map["DOGE"] ?: 0.0
    rates.QTUM = map["QTUM"] ?: 0.0

    return rates
}