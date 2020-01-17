package com.example.fincalc.ui.port.balance

import androidx.lifecycle.MutableLiveData
import com.example.fincalc.data.db.LoanType

object RepoLoansFilter {
     val _typeList = MutableLiveData<List<LoanType>?>()
     val _cur = MutableLiveData<List<String>>()
     val _sortByRate = MutableLiveData<Boolean?>()

}