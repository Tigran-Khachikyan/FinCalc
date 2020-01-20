package com.example.fincalc.ui.port.balance

import androidx.lifecycle.MutableLiveData
import com.example.fincalc.models.deposit.Frequency

object RepoDepFilter {
    val _frequencies = MutableLiveData<List<Frequency>?>()
    val _cur = MutableLiveData<List<String>>()
    val _sortByRate = MutableLiveData<Boolean?>()
}