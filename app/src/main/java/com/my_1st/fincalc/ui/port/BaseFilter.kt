package com.my_1st.fincalc.ui.port

import com.my_1st.fincalc.models.Banking
import com.my_1st.fincalc.ui.port.filter.SearchOption

abstract class BaseFilter(
    var bankingList: List<Banking>?,
    var sort: Boolean?,
    val searchOptions: MutableSet<SearchOption>
)
