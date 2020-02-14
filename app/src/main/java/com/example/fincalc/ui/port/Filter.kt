package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.ui.port.filter.Sort

abstract class Filter(
    var bankingList: List<Banking>?,
    var currencies: MutableSet<String>?,
    var sort: Sort
)
