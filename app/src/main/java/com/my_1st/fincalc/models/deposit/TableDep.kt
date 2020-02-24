package com.my_1st.fincalc.models.deposit

import com.my_1st.fincalc.data.db.dep.Deposit
import com.my_1st.fincalc.models.Calculator

class TableDep(val deposit: Deposit) {

    val rows: ArrayList<RowDep> = Calculator.getRowsDep(deposit)
    private val sumBasic: Long = deposit.amount
    var totalPercent: Double = Calculator.getTotalPerDep(rows)
    var totalPerAfterTax: Double = totalPercent * (100 - deposit.taxRate) / 100
    var totalPayment: Double = sumBasic + totalPerAfterTax
    val effectiveRate: Float = Calculator.getEffectiveRate(deposit)
}