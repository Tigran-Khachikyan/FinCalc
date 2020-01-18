package com.example.fincalc.ui.port.loans

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.db.Loan
import com.example.fincalc.data.repository.Repository
import com.example.fincalc.ui.port.NavViewModel
import kotlinx.coroutines.launch

class LoansViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository.getInstance(application)
    private val medLiveData = MediatorLiveData<List<Loan>>()
    //  fun getLoanList() = repo?.getLoans()

    private val selLoanLiveData = NavViewModel.Container.navSelLoanIdLD
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

    private fun combine(list: LiveData<List<Loan>>?, id: LiveData<Int?>): List<Loan>? {
        val listValue = list?.value
        val loanId = id.value
        return when {
            listValue != null && loanId != null -> {
                val newList = listValue as ArrayList<Loan>
                for (lo in listValue)
                    if (lo.id == loanId) {
                        newList.remove(lo)
                        newList.add(0, lo)
                        break
                    }
                newList
            }
            listValue != null && loanId == null -> {
                listValue
            }
            else -> null
        }
    }
}