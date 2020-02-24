package com.my_1st.fincalc.models.deposit

import com.my_1st.fincalc.models.Row

class RowDep : Row {

    override var curRowN: Int = 0
    override var balance: Double = 0.0
    override var percent: Double = 0.0
    override var payment: Double = 0.0
    var perAfterTax: Double = 0.0
}
