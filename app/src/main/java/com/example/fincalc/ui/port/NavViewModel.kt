package com.example.fincalc.ui.port

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fincalc.ui.port.NavViewModel.Container.navContLiveData

class NavViewModel : ViewModel() {


    object Container {
        val navContLiveData = MutableLiveData<NavSwitcher?>()
        val navSelLoanIdLD = MutableLiveData<Int?>()
        val navSelDepIdLD = MutableLiveData<Int?>()

        fun setNav(navSwitcher: NavSwitcher?, selLoanId: Int?) {

            if (navSwitcher == NavSwitcher.LOANS)
                navSelLoanIdLD.value = selLoanId
            else
                navSelDepIdLD.value = selLoanId

            navContLiveData.value = navSwitcher
        }
    }

    fun isSelectedLoan() = navContLiveData
}