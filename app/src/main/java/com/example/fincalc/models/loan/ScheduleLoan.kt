package com.example.fincalc.models.loan

class ScheduleLoan(val rowList: ArrayList<Row> = ArrayList(), var queryLoan: QueryLoan ) {

    class Row {
        var currentRowNumber: Int = 0
        var sumRemain: Double = 0.0
        var monthlyLoan: Double = 0.0
        var monthlyPercent: Double = 0.0
        var monthlyCommission: Double = 0.0
        var totalMonthlyPayment: Double = 0.0
    }

    var rowCount: Int = 0
    var sumBasic: Double = 0.0
    var oneTimeOtherCosts: Double = 0.0
    var oneTimeCommission: Double = 0.0
    var oneTimeComAndCosts: Double = oneTimeCommission + oneTimeOtherCosts
    var totalPercentSum: Double = 0.0
    var totalMonthlyCommissionPayment: Double = 0.0
    var totalAnnualCommissionPayment: Double = 0.0
    var totalPayment: Double = sumBasic + totalPercentSum + totalMonthlyCommissionPayment

    val realRate: Float
        get() {
            return ((totalPayment - sumBasic) * 24 / (sumBasic * (rowCount + 1))).toFloat()
        }
}