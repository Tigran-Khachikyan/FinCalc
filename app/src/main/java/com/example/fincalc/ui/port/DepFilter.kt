package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking
import com.example.fincalc.models.deposit.Frequency
import com.example.fincalc.ui.port.filter.Sort

class DepFilter(
    var freqList: List<Frequency>?,
    prodList: List<Banking>?,
    curList: MutableSet<String>?,
    sort: Sort
) : Filter(
    prodList,
    curList,
    sort
)