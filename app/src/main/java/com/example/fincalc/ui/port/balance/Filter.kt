package com.example.fincalc.ui.port.balance

import com.example.fincalc.models.Banking

abstract class Filter(
    var prodList: List<Banking>?,
    var curList: List<String>?,
    var sortByAcc: Boolean?
)
