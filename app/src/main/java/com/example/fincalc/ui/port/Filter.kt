package com.example.fincalc.ui.port

import com.example.fincalc.models.Banking

abstract class Filter(
    var bankingList: List<Banking>?,
    var currencies: List<String>?,
    var isSortedAscending: Boolean?
)
