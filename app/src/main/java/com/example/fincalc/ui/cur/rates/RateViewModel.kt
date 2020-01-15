package com.example.fincalc.ui.cur.rates

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fincalc.data.network.api_rates.ResponseCurApi
import com.example.fincalc.data.repository.Repository

class RateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    fun getLatestRates(): LiveData<ResponseCurApi> {
        Log.d("ggg", " result AMD in ViewModel: ${repository?.getRatesLatest()?.value?.rates?.AMD}")

        return repository?.getRatesLatest() as LiveData<ResponseCurApi>
    }

    /*   fun getHistoricalRates(): LiveData<ResponseCurApi> =
           repository.getHistoricalLatest(date)*/
}