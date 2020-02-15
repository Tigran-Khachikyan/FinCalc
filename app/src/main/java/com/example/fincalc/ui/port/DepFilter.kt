package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.ui.port.filter.SearchOption

class DepFilter(
    var freqList: List<Frequency>?,
    prodList: List<Banking>?,
    curList: MutableSet<String>?,
    sort: Boolean?,
    searchOption: MutableSet<SearchOption>
) : BaseFilter(
    prodList,
    curList,
    sort,
    searchOption
)