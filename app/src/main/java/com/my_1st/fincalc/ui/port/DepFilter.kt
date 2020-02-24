package com.my_1st.fincalc.ui.port

import com.my_1st.fincalc.models.Banking
import com.my_1st.fincalc.ui.port.filter.SearchOption

class DepFilter(
    prodList: List<Banking>?,
    sort: Boolean?,
    searchOption: MutableSet<SearchOption>
) : BaseFilter(
    prodList,
    sort,
    searchOption
)