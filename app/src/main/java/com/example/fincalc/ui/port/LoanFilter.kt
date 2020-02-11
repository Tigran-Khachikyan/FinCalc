package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType

class LoanFilter(
    var loanTypeList: List<LoanType>?,
    bankingList: List<Banking>?,
    currencies: List<String>?,
    isSortedAscending: Boolean?
) : Filter(
    bankingList,
    currencies,
    isSortedAscending
)