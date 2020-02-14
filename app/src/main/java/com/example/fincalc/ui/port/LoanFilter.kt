package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.ui.port.filter.Sort
import com.example.fincalc.ui.port.home.sortByRate

class LoanFilter(
    var loanTypeList: MutableSet<LoanType>?,
    bankingList: List<Banking>?,
    currencies: MutableSet<String>?,
    sort: Sort
) : Filter(
    bankingList,
    currencies,
    sort
)