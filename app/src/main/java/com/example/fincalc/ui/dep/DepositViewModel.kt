package com.example.fincalc.ui.dep

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fincalc.data.Repository
import com.example.fincalc.data.db.dep.Deposit
import kotlinx.coroutines.launch

class DepositViewModel(application: Application): AndroidViewModel(application) {

    private val repo = Repository.getInstance(application)

    fun addDep(dep:Deposit) = viewModelScope.launch {
        repo?.insertDep(dep)
    }

}