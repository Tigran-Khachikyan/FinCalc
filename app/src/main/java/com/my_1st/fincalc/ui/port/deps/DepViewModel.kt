package com.my_1st.fincalc.ui.port.deps

import android.app.Application
import androidx.lifecycle.*
import com.my_1st.fincalc.data.Repository
import com.my_1st.fincalc.data.db.dep.Deposit

class DepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    fun getDeposit(id: Int): LiveData<Deposit>? = repository?.getDepositById(id)
}