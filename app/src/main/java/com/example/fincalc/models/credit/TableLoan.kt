package com.example.fincalc.models.credit

import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.models.Calculator

data class TableLoan(
    val loan: Loan
) {
    var formulaLoan: Formula = loan.formula
    var rows: ArrayList<RowLoan> = Calculator.getRowsLoan(loan)
    var totalPercent: Double = Calculator.getTotalPerOrCom(true, rows)
    var totalComDuring: Double = Calculator.getTotalPerOrCom(false, rows)
    var rowCount: Int = loan.months
    var sumBasic: Long = loan.amount
    val oneTimeComAndCosts = Calculator.getOneTimeComAndCost(loan)
    var totalPayment: Double = sumBasic + totalComDuring + totalPercent
    var realRate: Float = if (sumBasic != 0L) {
        ((totalPayment - sumBasic) * 24 / (sumBasic * (rowCount + 1))).toFloat()
    } else 0F
}
