package com.example.fincalc.ui.port.loans

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.db.Loan
import com.example.fincalc.data.repository.Repository
import com.example.fincalc.ui.port.NaviViewModel
import kotlinx.coroutines.launch

class LoansViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository.getInstance(application)
    private val medLiveData = MediatorLiveData<List<Loan>>()
    //  fun getLoanList() = repo?.getLoans()

    private val selLoanLiveData = NaviViewModel.Container.navSelectedLoanLiveData
    private val loanListLiveData = repo?.getLoans()


    fun getLoanList(): LiveData<List<Loan>>? {
        viewModelScope.launch {
            loanListLiveData?.let {
                medLiveData.addSource(selLoanLiveData) {
                    medLiveData.value = combine(loanListLiveData, selLoanLiveData)
                }
                medLiveData.addSource(loanListLiveData) {
                    medLiveData.value = combine(loanListLiveData, selLoanLiveData)
                }
            }
        }
        return medLiveData
    }

    private fun combine(list: LiveData<List<Loan>>?, loan: LiveData<Loan?>): List<Loan>? {
        val listValue = list?.value
        val loanValue = loan.value
        return when {
            listValue != null && loanValue != null -> {
                val newList = listValue as ArrayList<Loan>
                if (newList.contains(loanValue)) {
                    newList.remove(loanValue)
                    newList.add(0, loanValue)
                }
                newList
            }
            listValue != null && loanValue == null -> {
                listValue
            }
            else -> null
        }
    }
}