package com.my_1st.fincalc.models.rates

import com.my_1st.fincalc.R
import com.my_1st.fincalc.data.network.api_crypto.CryptoRates

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

val mapCryptoNameIcon = hashMapOf(
    "BTC" to ("Bitcoin" to R.mipmap.cr_bitcoin),
    "ETH" to ("Ethereum" to R.mipmap.cr_etherum),
    "XRP" to ("XRP" to R.mipmap.cr_xrp),
    "BCH" to ("Bitcoin Cash" to R.mipmap.cr_bitcoin_cash),
    "USDT" to ("Tether" to R.mipmap.cr_tether),
    "LTC" to ("Litecoin" to R.mipmap.cr_litecoin),
    "EOS" to ("EOS" to R.mipmap.cr_eos),
    "BNB" to ("Binance Coin" to R.mipmap.cr_binance_coin),
    "ADA" to ("Cardano" to R.mipmap.cr_cardano),
    "ETC" to ("Ethereum Classic" to R.mipmap.cr_etherum_classic),
    "XMR" to ("Monero" to R.mipmap.cr_monero),
    "TRX" to ("TRON" to R.mipmap.cr_tron),
    "XLM" to ("Stellar" to R.mipmap.cr_stellar),
    "XTZ" to ("Tezos" to R.mipmap.cr_tezos),
    "DASH" to ("Dash" to R.mipmap.cr_dash),
    "LINK" to ("Chainlink" to R.mipmap.cr_chainlink),
    "LEO" to ("UNUS SED LEO" to R.mipmap.cr_unus_sed_leo_leo_logo),
    "MIOTA" to ("IOTA" to R.mipmap.cr_iota),
    "NEO" to ("Neo" to R.mipmap.cr_neo),
    "ZEC" to ("Zcash" to R.mipmap.cr_zcash),
    "MKR" to ("Maker" to R.mipmap.cr_maker),
    "XEM" to ("NEM" to R.mipmap.cr_nem),
    "BAT" to ("Basic Attention Token" to R.mipmap.cr_basic_attention_token),
    "DOGE" to ("Dogecoin" to R.mipmap.cr_dogecoin),
    "QTUM" to ("Qtum" to R.mipmap.cr_qtum)
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