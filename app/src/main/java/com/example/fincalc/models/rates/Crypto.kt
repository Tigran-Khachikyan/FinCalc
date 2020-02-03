package com.example.fincalc.models.rates

import com.example.fincalc.R
import com.example.fincalc.data.network.api_crypto.CryptoRates
import kotlinx.android.synthetic.main.recycler_rates.view.*

val cryptoNameMap = hashMapOf(
    "BTC" to "Bitcoin",
    "ETH" to "Ethereum",
    "XRP" to "XRP",
    "BCH" to "Bitcoin Cash",
    "USDT" to "Tether",
    "LTC" to "Litecoin",
    "EOS" to "EOS",
    "BNB" to "Binance Coin",
    "ADA" to "Cardano",
    "ETC" to "Ethereum \nClassic",
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
    "BAT" to "Basic Attention\n Token",
    "DOGE" to "Dogecoin",
    "QTUM" to "Qtum"
)

val cryptoPopularMap = hashMapOf(
    "BTC" to 1,
    "ETH" to 2,
    "XRP" to 3,
    "BCH" to 4,
    "USDT" to 5,
    "LTC" to 6,
    "EOS" to 7,
    "BNB" to 8,
    "ADA" to 9,
    "ETC" to 10,
    "XMR" to 11,
    "TRX" to 12,
    "XLM" to 13,
    "XTZ" to 14,
    "DASH" to 15,
    "LINK" to 16,
    "LEO" to 17,
    "MIOTA" to 18,
    "NEO" to 19,
    "ZEC" to 20,
    "MKR" to 21,
    "XEM" to 22,
    "BAT" to 23,
    "DOGE" to 24,
    "QTUM" to 25
)

val cryptoIconMap = hashMapOf(
    "BTC" to R.drawable.ic_filter,
    "ETH" to R.drawable.ic_filter,
    "XRP" to R.drawable.ic_filter,
    "BCH" to R.drawable.ic_filter,
    "USDT" to R.drawable.ic_filter,
    "LTC" to R.drawable.ic_filter,
    "EOS" to R.drawable.ic_filter,
    "BNB" to R.drawable.ic_filter,
    "ADA" to R.drawable.ic_filter,
    "ETC" to R.drawable.ic_filter,
    "XMR" to R.drawable.ic_filter,
    "TRX" to R.drawable.ic_filter,
    "XLM" to R.drawable.ic_filter,
    "XTZ" to R.drawable.ic_filter,
    "DASH" to R.drawable.ic_filter,
    "LINK" to R.drawable.ic_filter,
    "LEO" to R.drawable.ic_filter,
    "MIOTA" to R.drawable.ic_filter,
    "NEO" to R.drawable.ic_filter,
    "ZEC" to R.drawable.ic_filter,
    "MKR" to R.drawable.ic_filter,
    "XEM" to R.drawable.ic_filter,
    "BAT" to R.drawable.ic_filter,
    "DOGE" to R.drawable.ic_filter,
    "QTUM" to R.drawable.ic_filter
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