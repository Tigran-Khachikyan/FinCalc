package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.models.credit.LoanType
import com.example.fincalc.ui.port.filter.SearchOption

class LoanFilter(
    var loanTypeList: MutableSet<LoanType>?,
    bankingList: List<Banking>?,
    currencies: MutableSet<String>?,
    sort: Boolean?,
    searchOption: MutableSet<SearchOption>
) : BaseFilter(
    bankingList,
    currencies,
    sort,
    searchOption
)