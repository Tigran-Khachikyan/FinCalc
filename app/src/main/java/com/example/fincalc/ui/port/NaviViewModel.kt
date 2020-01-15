package com.example.fincalc.ui.port

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fincalc.data.db.Loan
import com.example.fincalc.ui.port.NaviViewModel.Container.navContLiveData

class NaviViewModel : ViewModel() {


    object Container {
        val navContLiveData = MutableLiveData<Boolean?>()
        val navSelectedLoanLiveData = MutableLiveData<Loan?>()

        fun setNavi(fromLoans: Boolean, selectedLoan: Loan) {
            navContLiveData.value = fromLoans
            navSelectedLoanLiveData.value = selectedLoan
        }
    }
    fun isSelectedLoan() = navContLiveData
}