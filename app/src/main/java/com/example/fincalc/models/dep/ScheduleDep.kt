package com.example.fincalc.models.dep

class ScheduleDep(var rowList: ArrayList<Row> = ArrayList()) {

    class Row {
        var currentRowNumber: Int = 0
        var balance: Double = 0.0
        var percent: Double = 0.0
        var percentAfterTaxing: Double = 0.0
        var payment: Double = 0.0
    }

    var sumBasic: Long = 0
    var totalPercent: Double = 0.0
    var totalPercentAfterTaxing: Double = 0.0
    var totalPayment: Double = 0.0
}