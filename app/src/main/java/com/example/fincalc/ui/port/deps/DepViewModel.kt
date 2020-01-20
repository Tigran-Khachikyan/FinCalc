package com.example.fincalc.ui.port.deps

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.Repository
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.ui.port.NavViewModel
import kotlinx.coroutines.launch

class DepViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = Repository.getInstance(application)
    private val medLiveData = MediatorLiveData<List<Deposit>>()
    //  fun getLoanList() = repo?.getLoans()

    private val selDepLiveData = NavViewModel.Container.navSelLoanIdLD
    private val depListLiveData = repo?.getDep()

    fun getDepList(): LiveData<List<Deposit>>? {
        viewModelScope.launch {
            depListLiveData?.let {
                medLiveData.addSource(selDepLiveData) {
                    medLiveData.value = combine(depListLiveData, selDepLiveData)
                }
                medLiveData.addSource(depListLiveData) {
                    medLiveData.value = combine(depListLiveData, selDepLiveData)
                }
            }
        }
        return medLiveData
    }

    private fun combine(list: LiveData<List<Deposit>>?, id: LiveData<Int?>): List<Deposit>? {
        val listValue = list?.value
        val depId = id.value
        return when {
            listValue != null && depId != null -> {
                val newList = listValue as ArrayList<Deposit>
                for (dep in listValue)
                    if (dep.id == depId) {
                        newList.remove(dep)
                        newList.add(0, dep)
                        break
                    }
                newList
            }
            listValue != null && depId == null -> {
                listValue
            }
            else -> null
        }
    }
}