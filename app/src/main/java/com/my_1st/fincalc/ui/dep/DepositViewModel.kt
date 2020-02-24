package com.my_1st.fincalc.ui.dep

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.my_1st.fincalc.data.Repository
import com.my_1st.fincalc.data.db.dep.Deposit
import kotlinx.coroutines.launch

class DepositViewModel(application: Application): AndroidViewModel(application) {

    private val repo = Repository.getInstance(application)

    fun addDep(dep:Deposit) = viewModelScope.launch {
        repo?.insertDep(dep)
    }
}