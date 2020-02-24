package com.my_1st.fincalc.models.credit

import com.my_1st.fincalc.models.Row

class RowLoan : Row {
    override var curRowN: Int = 0
    override var balance: Double = 0.0
    override var percent: Double = 0.0
    override var payment: Double = 0.0
    var monthLoan: Double = 0.0
    var monthCom: Double = 0.0
}