package com.example.fincalc.ui.port.balance

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.db.Loan
import com.example.fincalc.data.db.LoanType
import com.example.fincalc.data.repository.Repository
import kotlinx.coroutines.launch

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    fun getLoans() = repository?.getLoans()

    fun getLoansByType(type: LoanType) = repository?.getLoansByType(type)

    fun getLoanById(id: Int) = repository?.getLoanById(id)

    fun addLoan(loan: Loan) = viewModelScope.launch {
        repository?.insertLoan(loan)
    }

    fun deleteLoan(loan: Loan) = viewModelScope.launch {
        repository?.deleteLoan(loan)
    }

    fun deleteAllLoans() = viewModelScope.launch {
        repository?.deleteAllLoans()
    }
}