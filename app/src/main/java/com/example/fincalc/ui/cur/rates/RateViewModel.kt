package com.example.fincalc.ui.cur.rates

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fincalc.data.network.api_rates.ResponseCurApi
import com.example.fincalc.data.Repository
import com.example.fincalc.data.network.firebase.RatesUi
import kotlinx.coroutines.launch

class RateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    /* fun getLatestRates(): LiveData<ResponseCurApi> {
         Log.d("ggg", " result AMD in ViewModel: ${repository?.getRatesLatest()?.value?.rates?.AMD}")

         return repository?.getRatesLatest() as LiveData<ResponseCurApi>
     }*/

    /*fun getHistoricalRates(): LiveData<ResponseCurApi> =
         repository.getHistoricalLatest(date)*/

    private var latCurRates = repository?.getLatestCurRates()
    fun getLatCurRates(): LiveData<RatesUi>? {
        // return latCurRates as LiveData<ResponseCurApi>
        return latCurRates
    }

    //fun getHisCurRates(date: String): LiveData<RatesUi>? = repository?.getHisCurRates(date)

}