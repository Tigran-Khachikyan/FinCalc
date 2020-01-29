package com.example.fincalc.ui.cur.rates

import android.app.Application
import androidx.lifecycle.*
import com.example.fincalc.data.network.api_rates.ResponseCurApi
import com.example.fincalc.data.Repository
import com.example.fincalc.data.network.firebase.RatesUi
import com.example.fincalc.models.cur_met.BaseCurrency
import com.example.fincalc.models.cur_met.getMapCurRates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RatesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)
    private val latCurRates = repository?.getLatestCurRates()
    private var latTableRates = MediatorLiveData<BaseCurrency>()

    private fun getLatCurRates(): LiveData<RatesUi>? = latCurRates

    fun setLatTableCur(cur: String) {
        TableRatesRepo.latTableCur.value = cur
    }

    fun getLatTableRates(): LiveData<BaseCurrency?> {
        viewModelScope.launch {
            latCurRates?.let {
                latTableRates.addSource(latCurRates) {
                    latTableRates.value = combineLiveData(latCurRates, TableRatesRepo.latTableCur)
                }
                latTableRates.addSource(TableRatesRepo.latTableCur) {
                    latTableRates.value = combineLiveData(latCurRates, TableRatesRepo.latTableCur)
                }
            }
        }
        return latTableRates
    }

    private fun combineLiveData(ratesUi: LiveData<RatesUi>, cur: LiveData<String>?): BaseCurrency? {
        val rates = ratesUi.value
        val currency = cur?.value
        return if (rates != null && currency != null) BaseCurrency(currency, rates) else null
    }


    object TableRatesRepo {
        val latTableCur = MutableLiveData<String>()
    }

}