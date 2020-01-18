package com.example.fincalc.ui.port

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fincalc.ui.port.NavViewModel.Container.navContLiveData

class NavViewModel : ViewModel() {


    object Container {
        val navContLiveData = MutableLiveData<NavSwitcher?>()
        val navSelLoanIdLD = MutableLiveData<Int?>()

        fun setNav(navSwitcher: NavSwitcher?, selLoanId: Int?) {
            navContLiveData.value = navSwitcher
            navSelLoanIdLD.value = selLoanId
        }
    }
    fun isSelectedLoan() = navContLiveData
}