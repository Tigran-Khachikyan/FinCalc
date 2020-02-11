package com.example.fincalc.ui.port.deps

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import kotlinx.coroutines.launch

class DepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    fun getDeposit(id: Int): LiveData<Deposit>? = repository?.getDepositById(id)
}