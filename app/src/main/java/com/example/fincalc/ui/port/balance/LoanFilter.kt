package com.example.fincalc.ui.port.balance

import com.example.fincalc.data.db.Loan
import com.example.fincalc.data.db.LoanType

class LoanFilter(
    var loanList: List<Loan>?,
    var filtTypeList: List<LoanType>?,
    var filtCur: List<String>?,
    var sortByAcc: Boolean?
)