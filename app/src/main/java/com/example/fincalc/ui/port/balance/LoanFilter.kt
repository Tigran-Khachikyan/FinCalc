package com.example.fincalc.ui.port.balance

import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType

class LoanFilter(
    var filTypeList: List<LoanType>?,
    prodList: List<Banking>?,
    curList: List<String>?,
    sortByAcc: Boolean?
) : Filter(prodList, curList, sortByAcc)