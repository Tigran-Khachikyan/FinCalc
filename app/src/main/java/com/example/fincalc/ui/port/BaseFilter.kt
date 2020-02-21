package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.ui.port.filter.SearchOption

abstract class BaseFilter(
    var bankingList: List<Banking>?,
    var sort: Boolean?,
    val searchOptions: MutableSet<SearchOption>
)
