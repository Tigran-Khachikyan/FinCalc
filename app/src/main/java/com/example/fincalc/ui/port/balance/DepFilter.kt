package com.example.fincalc.ui.port.balance

import com.example.fincalc.models.Banking
import com.example.fincalc.models.deposit.Frequency

class DepFilter(
    var freqList: List<Frequency>?,
    prodList: List<Banking>?,
    curList: List<String>?,
    sortByAcc: Boolean?
) : Filter(prodList, curList, sortByAcc)