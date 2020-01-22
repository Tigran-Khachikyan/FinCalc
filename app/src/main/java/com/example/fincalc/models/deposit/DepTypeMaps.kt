package com.example.fincalc.models.deposit

import android.content.Context
import com.example.fincalc.R

fun getFreqListName(context: Context): ArrayList<String> {
    val arrayList = ArrayList<String>()

    for (int in Frequency.values()) {
        val name = context.resources.getString(int.id)
        arrayList.add(name)
    }
    return arrayList
}

fun getFreqFromSelec(string: String, context: Context?): Frequency? {

    var freq: Frequency? = null

    for (id in frequencyMap.keys) {
        if (string == context?.resources?.getString(id)) {
            freq = frequencyMap[id]!!
            break
        }
    }
    return freq
}

val frequencyMap = hashMapOf(
    R.string.MonthlyPaymentDep to Frequency.MONTHLY,
    R.string.QuarterlyPaymentDep to Frequency.QUARTERLY,
    R.string.AtTheEndPayment to Frequency.AT_THE_END,
    R.string.OTHER to Frequency.OTHER
)

