package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.ui.port.filter.SearchOption

class DepFilter(
    prodList: List<Banking>?,
    sort: Boolean?,
    searchOption: MutableSet<SearchOption>
) : BaseFilter(
    prodList,
    sort,
    searchOption
)