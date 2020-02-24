package com.my_1st.fincalc.models.rates

import com.my_1st.fincalc.R
import com.my_1st.fincalc.data.network.api_cur_metal.CurMetRates

fun getMapFromRates(rates: CurMetRates): HashMap<String, Double>? = hashMapOf(
    "AED" to rates.AED,
    "AMD" to rates.AMD,
    "ARS" to rates.ARS,
    "AUD" to rates.AUD,
    "BOB" to rates.BOB,
    "BRL" to rates.BRL,
    "BYN" to rates.BYN,
    "CAD" to rates.CAD,
    "CHF" to rates.CHF,
    "CNY" to rates.CNY,
    "CUC" to rates.CUC,
    "DKK" to rates.DKK,
    "EGP" to rates.EGP,
    "EUR" to rates.EUR,
    "GBP" to rates.GBP,
    "GEL" to rates.GEL,
    "HKD" to rates.HKD,
    "IDR" to rates.IDR,
    "ILS" to rates.ILS,
    "INR" to rates.INR,
    "IQD" to rates.IQD,
    "IRR" to rates.IRR,
    "JPY" to rates.JPY,
    "KPW" to rates.KPW,
    "KRW" to rates.KRW,
    "KWD" to rates.KWD,
    "KZT" to rates.KZT,
    "LBP" to rates.LBP,
    "MAD" to rates.MAD,
    "MXN" to rates.MXN,
    "MYR" to rates.MYR,
    "NGN" to rates.NGN,
    "NOK" to rates.NOK,
    "NZD" to rates.NZD,
    "OMR" to rates.OMR,
    "PEN" to rates.PEN,
    "PHP" to rates.PHP,
    "PYG" to rates.PYG,
    "QAR" to rates.QAR,
    "RSD" to rates.RSD,
    "RUB" to rates.RUB,
    "SAR" to rates.SAR,
    "SEK" to rates.SEK,
    "SGD" to rates.SGD,
    "SYP" to rates.SYP,
    "THB" to rates.THB,
    "TMT" to rates.TMT,
    "TND" to rates.TND,
    "TRY" to rates.TRY,
    "UAH" to rates.UAH,
    "USD" to rates.USD,
    "UYU" to rates.UYU,
    "UZS" to rates.UZS,
    "VES" to rates.VES,
    "VND" to rates.VND,
    "ZAR" to rates.ZAR,
    "XAG" to rates.XAG,
    "XAU" to rates.XAU,
    "XPD" to rates.XPD,
    "XPT" to rates.XPT
)

fun getRatesFromMap(map: HashMap<String, Double>): CurMetRates {
    val rates = CurMetRates()

    rates.AED = map["AED"] ?: 0.0
    rates.AMD = map["AMD"] ?: 0.0
    rates.ARS = map["ARS"] ?: 0.0
    rates.AUD = map["AUD"] ?: 0.0
    rates.BOB = map["BOB"] ?: 0.0
    rates.BRL = map["BRL"] ?: 0.0
    rates.BYN = map["BYN"] ?: 0.0
    rates.CAD = map["CAD"] ?: 0.0
    rates.CHF = map["CHF"] ?: 0.0
    rates.CNY = map["CNY"] ?: 0.0
    rates.CUC = map["CUC"] ?: 0.0
    rates.DKK = map["DKK"] ?: 0.0
    rates.EGP = map["EGP"] ?: 0.0
    rates.EUR = map["EUR"] ?: 0.0
    rates.GBP = map["GBP"] ?: 0.0
    rates.GEL = map["GEL"] ?: 0.0
    rates.HKD = map["HKD"] ?: 0.0
    rates.IDR = map["IDR"] ?: 0.0
    rates.ILS = map["ILS"] ?: 0.0
    rates.INR = map["INR"] ?: 0.0
    rates.IQD = map["IQD"] ?: 0.0
    rates.IRR = map["IRR"] ?: 0.0
    rates.JPY = map["JPY"] ?: 0.0
    rates.KPW = map["KPW"] ?: 0.0
    rates.KRW = map["KRW"] ?: 0.0
    rates.KWD = map["KWD"] ?: 0.0
    rates.KZT = map["KZT"] ?: 0.0
    rates.LBP = map["LBP"] ?: 0.0
    rates.MAD = map["MAD"] ?: 0.0
    rates.MXN = map["MXN"] ?: 0.0
    rates.MYR = map["MYR"] ?: 0.0
    rates.NGN = map["NGN"] ?: 0.0
    rates.NOK = map["NOK"] ?: 0.0
    rates.NZD = map["NZD"] ?: 0.0
    rates.OMR = map["OMR"] ?: 0.0
    rates.PEN = map["PEN"] ?: 0.0
    rates.PHP = map["PHP"] ?: 0.0
    rates.PYG = map["PYG"] ?: 0.0
    rates.QAR = map["QAR"] ?: 0.0
    rates.RSD = map["RSD"] ?: 0.0
    rates.RUB = map["RUB"] ?: 0.0
    rates.SAR = map["SAR"] ?: 0.0
    rates.SEK = map["SEK"] ?: 0.0
    rates.SGD = map["SGD"] ?: 0.0
    rates.SYP = map["SYP"] ?: 0.0
    rates.THB = map["THB"] ?: 0.0
    rates.TMT = map["TMT"] ?: 0.0
    rates.TND = map["TND"] ?: 0.0
    rates.TRY = map["TRY"] ?: 0.0
    rates.UAH = map["UAH"] ?: 0.0
    rates.USD = map["USD"] ?: 0.0
    rates.UYU = map["UYU"] ?: 0.0
    rates.UZS = map["UZS"] ?: 0.0
    rates.VES = map["VES"] ?: 0.0
    rates.VND = map["VND"] ?: 0.0
    rates.ZAR = map["ZAR"] ?: 0.0
    rates.XAG = map["XAG"] ?: 0.0
    rates.XAU = map["XAU"] ?: 0.0
    rates.XPD = map["XPD"] ?: 0.0
    rates.XPT = map["XPT"] ?: 0.0
    return rates
}

val mapRatesNameIcon = hashMapOf(
    "AED" to (R.string.AED to R.drawable.ic_united_arab_emirates),
    "AMD" to (R.string.AMD to R.drawable.ic_armenia),
    "ARS" to (R.string.ARS to R.drawable.ic_argentina),
    "AUD" to (R.string.AUD to R.drawable.ic_australia),
    "BOB" to (R.string.BOB to R.drawable.ic_bolivia),
    "BRL" to (R.string.BRL to R.drawable.ic_brazil),
    "BYN" to (R.string.BYN to R.drawable.ic_belarus),
    "CAD" to (R.string.CAD to R.drawable.ic_canada),
    "CHF" to (R.string.CHF to R.drawable.ic_switzerland),
    "CNY" to (R.string.CNY to R.drawable.ic_china),
    "CUC" to (R.string.CUC to R.drawable.ic_cuba),
    "DKK" to (R.string.DKK to R.drawable.ic_denmark),
    "EGP" to (R.string.EGP to R.drawable.ic_egypt),
    "EUR" to (R.string.EUR to R.drawable.ic_european_union),
    "GBP" to (R.string.GBP to R.drawable.ic_uk),
    "GEL" to (R.string.GEL to R.drawable.ic_georgia),
    "HKD" to (R.string.HKD to R.drawable.ic_hong_kong),
    "IDR" to (R.string.IDR to R.drawable.ic_indonesia),
    "ILS" to (R.string.ILS to R.drawable.ic_israel),
    "INR" to (R.string.INR to R.drawable.ic_india),
    "IQD" to (R.string.IQD to R.drawable.ic_iraq),
    "IRR" to (R.string.IRR to R.drawable.ic_iran),
    "JPY" to (R.string.JPY to R.drawable.ic_japan),
    "KPW" to (R.string.KPW to R.drawable.ic_north_korea),
    "KRW" to (R.string.KRW to R.drawable.ic_south_korea),
    "KWD" to (R.string.KWD to R.drawable.ic_kuwait),
    "KZT" to (R.string.KZT to R.drawable.ic_kazakhstan),
    "LBP" to (R.string.LBP to R.drawable.ic_lebanon),
    "MAD" to (R.string.MAD to R.drawable.ic_morocco),
    "MXN" to (R.string.MXN to R.drawable.ic_mexico),
    "MYR" to (R.string.MYR to R.drawable.ic_malaysia),
    "NGN" to (R.string.NGN to R.drawable.ic_nigeria),
    "NOK" to (R.string.NOK to R.drawable.ic_norway),
    "NZD" to (R.string.NZD to R.drawable.ic_new_zealand),
    "OMR" to (R.string.OMR to R.drawable.ic_oman),
    "PEN" to (R.string.PEN to R.drawable.ic_peru),
    "PHP" to (R.string.PHP to R.drawable.ic_philippines),
    "PYG" to (R.string.PYG to R.drawable.ic_paraguay),
    "QAR" to (R.string.QAR to R.drawable.ic_qatar),
    "RSD" to (R.string.RSD to R.drawable.ic_serbia),
    "RUB" to (R.string.RUB to R.drawable.ic_russia),
    "SAR" to (R.string.SAR to R.drawable.ic_saudi_arabia),
    "SEK" to (R.string.SEK to R.drawable.ic_sweden),
    "SGD" to (R.string.SGD to R.drawable.ic_singapore),
    "SYP" to (R.string.SYP to R.drawable.ic_syria),
    "THB" to (R.string.THB to R.drawable.ic_thailand),
    "TMT" to (R.string.TMT to R.drawable.ic_turkmenistan),
    "TND" to (R.string.TND to R.drawable.ic_tunisia),
    "TRY" to (R.string.TRY to R.drawable.ic_turkey),
    "UAH" to (R.string.UAH to R.drawable.ic_ukraine),
    "USD" to (R.string.USD to R.drawable.ic_usa),
    "UYU" to (R.string.UYU to R.drawable.ic_uruguay),
    "UZS" to (R.string.UZS to R.drawable.ic_uzbekistan),
    "VES" to (R.string.VES to R.drawable.ic_venezuela),
    "VND" to (R.string.VND to R.drawable.ic_vietnam),
    "ZAR" to (R.string.ZAR to R.drawable.ic_south_africa),
    "XAG" to (R.string.XAG to R.mipmap.silver),
    "XAU" to (R.string.XAU to R.mipmap.aurum),
    "XPD" to (R.string.XPD to R.mipmap.palladium),
    "XPT" to (R.string.XPT to R.mipmap.platinum)
)

val arrayCurCodes = arrayOf(
    "USD",
    "EUR",
    "GBP",
    "CNY",
    "RUB",
    "AMD",
    "AED",
    "ARS",
    "AUD",
    "BOB",
    "BRL",
    "BYN",
    "CAD",
    "CHF",
    "CUC",
    "DKK",
    "EGP",
    "GEL",
    "HKD",
    "IDR",
    "ILS",
    "INR",
    "IQD",
    "IRR",
    "JPY",
    "KPW",
    "KRW",
    "KWD",
    "KZT",
    "LBP",
    "MAD",
    "MXN",
    "MYR",
    "NGN",
    "NOK",
    "NZD",
    "OMR",
    "PEN",
    "PHP",
    "PYG",
    "QAR",
    "RSD",
    "SAR",
    "SEK",
    "SGD",
    "SYP",
    "THB",
    "TMT",
    "TND",
    "TRY",
    "UAH",
    "UYU",
    "UZS",
    "VES",
    "VND",
    "ZAR"
)





