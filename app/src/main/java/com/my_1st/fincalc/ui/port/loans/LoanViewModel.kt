package com.my_1st.fincalc.ui.port.loans

import android.app.Application
import androidx.lifecycle.*
import com.my_1st.fincalc.data.db.loan.Loan
import com.my_1st.fincalc.data.Repository

class LoanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)
    fun getLoan(id: Int): LiveData<Loan>? = repository?.getLoanById(id)
}