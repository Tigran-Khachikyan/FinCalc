package com.example.fincalc.models.deposit

import com.example.fincalc.models.Row

class RowDep : Row {

    override var curRowN: Int = 0
    override var balance: Double = 0.0
    override var percent: Double = 0.0
    override var payment: Double = 0.0
    var percAfterTax: Double = 0.0
}
