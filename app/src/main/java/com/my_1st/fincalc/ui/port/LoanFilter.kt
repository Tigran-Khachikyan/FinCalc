package com.my_1st.fincalc.ui.port

import com.my_1st.fincalc.models.Banking
import com.my_1st.fincalc.ui.port.filter.SearchOption

class LoanFilter(
    bankingList: List<Banking>?,
    sort: Boolean?,
    searchOption: MutableSet<SearchOption>
) : BaseFilter(
    bankingList,
    sort,
    searchOption
)