package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.ui.port.filter.SearchOption

class LoanFilter(
    bankingList: List<Banking>?,
    sort: Boolean?,
    searchOption: MutableSet<SearchOption>
) : BaseFilter(
    bankingList,
    sort,
    searchOption
)