package com.example.fincalc.models.credit

import android.content.Context

fun getLoanTypesNames(context: Context): Array<String> {
    val arrayList = ArrayList<String>()
    for (int in LoanType.values()) {
        val name = context.resources.getString(int.id)
        arrayList.add(name)
    }
    return arrayList.toTypedArray()
}

fun getLoanTypeFromString(string: String, context: Context): LoanType? {
    var result: LoanType? = null

    for (type in LoanType.values()) {
        if (string == context.resources.getString(type.id)) {
            result = type
            break
        }
    }
    return result
}


