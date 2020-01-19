package com.example.fincalc.models.credit

import android.content.Context
import com.example.fincalc.R

fun getLoanTypeListName(context: Context): ArrayList<String> {
    val arrayList = ArrayList<String>()

    for (int in LoanType.values()) {
        val name = context.resources.getString(int.id)
        arrayList.add(name)
    }
    return arrayList
}

fun getEnumFromSelection(string: String, context: Context?): LoanType {

    var type = LoanType.OTHER

    for (id in loanTypeMap.keys) {
        if (string == context?.resources?.getString(id)) {
            type = loanTypeMap[id]!!
            break
        }
    }
    return type
}

val loanTypeMap = hashMapOf(
    R.string.GOLD_PLEDGE_SECURED to LoanType.GOLD_PLEDGE_SECURED,
    R.string.CAR_LOAN to LoanType.CAR_LOAN,
    R.string.MORTGAGE to LoanType.MORTGAGE,
    R.string.CONSUMER_LOAN to LoanType.CONSUMER_LOAN,
    R.string.STUDENT_LOAN to LoanType.STUDENT_LOAN,
    R.string.UNSECURED to LoanType.UNSECURED,
    R.string.CREDIT_LINES to LoanType.CREDIT_LINES,
    R.string.DEPOSIT_SECURED to LoanType.DEPOSIT_SECURED,
    R.string.BUSINESS to LoanType.BUSINESS
)

