package com.example.fincalc.models.cur_met

import com.example.fincalc.R
import com.example.fincalc.data.network.api_rates.Rates

fun getMapCurRates(rates: Rates): HashMap<String, Double>? = hashMapOf(
    "AMD" to rates.AMD,
    "AED" to rates.AED,
    "ARS" to rates.ARS,
    "AUD" to rates.AUD,
    "BOB" to rates.BOB,
    "BRL" to rates.BRL,
    "BYN" to rates.BYN,
    "CAD" to rates.CAD,
    "CHF" to rates.CHF,
    "CNY" to rates.CNY,
    "CUC" to rates.CUC,
    "CUP" to rates.CUP,
    "EGP" to rates.EGP,
    "EUR" to rates.EUR,
    "GBP" to rates.GBP,
    "GEL" to rates.GEL,
    "ILS" to rates.ILS,
    "INR" to rates.INR,
    "IQD" to rates.IQD,
    "IRR" to rates.IRR,
    "JPY" to rates.JPY,
    "KRW" to rates.KRW,
    "KWD" to rates.KWD,
    "LBP" to rates.LBP,
    "MAD" to rates.MAD,
    "MXN" to rates.MXN,
    "NZD" to rates.NZD,
    "PEN" to rates.PEN,
    "QAR" to rates.QAR,
    "RUB" to rates.RUB,
    "SAR" to rates.SAR,
    "SEK" to rates.SEK,
    "SGD" to rates.SGD,
    "SYP" to rates.SYP,
    "THB" to rates.THB,
    "TND" to rates.TND,
    "TRY" to rates.TRY,
    "UAH" to rates.UAH,
    "USD" to rates.USD,
    "UYU" to rates.UYU,
    "VND" to rates.VND,
    "ZAR" to rates.ZAR
)

fun getRatesFromMap(map: HashMap<String, Double>): Rates {
    val rates = Rates()

    rates.AMD = map["AMD"] ?: 0.0
    rates.AED = map["AED"] ?: 0.0
    rates.ARS = map["ARS"] ?: 0.0
    rates.AUD = map["AUD"] ?: 0.0
    rates.BOB = map["BOB"] ?: 0.0
    rates.BRL = map["BRL"] ?: 0.0
    rates.BYN = map["BYN"] ?: 0.0
    rates.CAD = map["CAD"] ?: 0.0
    rates.CHF = map["CHF"] ?: 0.0
    rates.CNY = map["CNY"] ?: 0.0
    rates.CUC = map["CUC"] ?: 0.0
    rates.CUP = map["CUP"] ?: 0.0
    rates.EGP = map["EGP"] ?: 0.0
    rates.EUR = map["EUR"] ?: 0.0
    rates.GBP = map["GBP"] ?: 0.0
    rates.GEL = map["GEL"] ?: 0.0
    rates.ILS = map["ILS"] ?: 0.0
    rates.INR = map["INR"] ?: 0.0
    rates.IQD = map["IQD"] ?: 0.0
    rates.IRR = map["IRR"] ?: 0.0
    rates.JPY = map["JPY"] ?: 0.0
    rates.KRW = map["KRW"] ?: 0.0
    rates.KWD = map["KWD"] ?: 0.0
    rates.LBP = map["LBP"] ?: 0.0
    rates.MAD = map["MAD"] ?: 0.0
    rates.MXN = map["MXN"] ?: 0.0
    rates.NZD = map["NZD"] ?: 0.0
    rates.PEN = map["PEN"] ?: 0.0
    rates.QAR = map["QAR"] ?: 0.0
    rates.RUB = map["RUB"] ?: 0.0
    rates.SAR = map["SAR"] ?: 0.0
    rates.SEK = map["SEK"] ?: 0.0
    rates.SGD = map["SGD"] ?: 0.0
    rates.SYP = map["SYP"] ?: 0.0
    rates.THB = map["THB"] ?: 0.0
    rates.TND = map["TND"] ?: 0.0
    rates.TRY = map["TRY"] ?: 0.0
    rates.UAH = map["UAH"] ?: 0.0
    rates.USD = map["USD"] ?: 0.0
    rates.UYU = map["UYU"] ?: 0.0
    rates.VND = map["VND"] ?: 0.0
    rates.ZAR = map["ZAR"] ?: 0.0
    return rates
}

val currencyCodeList = arrayOf(
    "AMD",
    "AED",
    "ARS",
    "AUD",
    "BOB",
    "BRL",
    "BYN",
    "CAD",
    "CHF",
    "CNY",
    "CUC",
    "CUP",
    "EGP",
    "EUR",
    "GBP",
    "GEL",
    "ILS",
    "INR",
    "IQD",
    "IRR",
    "JPY",
    "KRW",
    "KWD",
    "LBP",
    "MAD",
    "MXN",
    "NZD",
    "PEN",
    "QAR",
    "RUB",
    "SAR",
    "SEK",
    "SGD",
    "SYP",
    "THB",
    "TND",
    "TRY",
    "UAH",
    "USD",
    "UYU",
    "VND",
    "ZAR"
)

val currencyFlagList = arrayOf(
    R.mipmap.armenia,
    R.mipmap.united_arab_emirates,
    R.mipmap.argentina,
    R.mipmap.australia,
    R.mipmap.bolivia,
    R.mipmap.brazil,
    R.mipmap.belarus,
    R.mipmap.canada,
    R.mipmap.switzerland,
    R.mipmap.china,
    R.mipmap.cuba,
    R.mipmap.cuba,
    R.mipmap.egypt,
    R.mipmap.european_union,
    R.mipmap.united_kingdom,
    R.mipmap.vrastan,
    R.mipmap.israel,
    R.mipmap.india,
    R.mipmap.iraq,
    R.mipmap.iran,
    R.mipmap.japan,
    R.mipmap.south_korea,
    R.mipmap.kuwait,
    R.mipmap.lebanon,
    R.mipmap.morocco,
    R.mipmap.mexico,
    R.mipmap.new_zealand,
    R.mipmap.peru,
    R.mipmap.qatar,
    R.mipmap.russia,
    R.mipmap.saudi_arabia,
    R.mipmap.sweden,
    R.mipmap.singapore,
    R.mipmap.syria,
    R.mipmap.thailand,
    R.mipmap.tunisia,
    R.mipmap.turkey,
    R.mipmap.ukraine,
    R.mipmap.united_states,
    R.mipmap.uruguay,
    R.mipmap.vietnam,
    R.mipmap.south_africa
)


val currencyMapFlags = hashMapOf(
    "AMD" to R.mipmap.armenia,
    "AED" to R.mipmap.united_arab_emirates,
    "ARS" to R.mipmap.argentina,
    "AUD" to R.mipmap.australia,
    "BOB" to R.mipmap.bolivia,
    "BRL" to R.mipmap.brazil,
    "BYN" to R.mipmap.belarus,
    "CAD" to R.mipmap.canada,
    "CHF" to R.mipmap.switzerland,
    "CNY" to R.mipmap.china,
    "CUC" to R.mipmap.cuba,
    "CUP" to R.mipmap.cuba,
    "EGP" to R.mipmap.egypt,
    "EUR" to R.mipmap.european_union,
    "GBP" to R.mipmap.united_kingdom,
    "GEL" to R.mipmap.vrastan,
    "ILS" to R.mipmap.israel,
    "INR" to R.mipmap.india,
    "IQD" to R.mipmap.iraq,
    "IRR" to R.mipmap.iran,
    "JPY" to R.mipmap.japan,
    "KRW" to R.mipmap.south_korea,
    "KWD" to R.mipmap.kuwait,
    "LBP" to R.mipmap.lebanon,
    "MAD" to R.mipmap.morocco,
    "MXN" to R.mipmap.mexico,
    "NZD" to R.mipmap.new_zealand,
    "PEN" to R.mipmap.peru,
    "QAR" to R.mipmap.qatar,
    "RUB" to R.mipmap.russia,
    "SAR" to R.mipmap.saudi_arabia,
    "SEK" to R.mipmap.sweden,
    "SGD" to R.mipmap.singapore,
    "SYP" to R.mipmap.syria,
    "THB" to R.mipmap.thailand,
    "TND" to R.mipmap.tunisia,
    "TRY" to R.mipmap.turkey,
    "UAH" to R.mipmap.ukraine,
    "USD" to R.mipmap.united_states,
    "UYU" to R.mipmap.uruguay,
    "VND" to R.mipmap.vietnam,
    "ZAR" to R.mipmap.south_africa
//42 flags
)




