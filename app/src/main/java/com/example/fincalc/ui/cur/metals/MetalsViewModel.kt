package com.example.fincalc.ui.cur.metals

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fincalc.data.network.api_metals.ResponseMetalApi
import com.example.fincalc.data.network.api_rates.ResponseCurApi
import com.example.fincalc.data.repository.Repository

@Suppress("UNCHECKED_CAST")
class MetalsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    fun getLatestMetals(): LiveData<ResponseMetalApi> {
        Log.d("ggg", " result AMD in ViewModel: ${repository?.getMetalLatest()?.value?.rates?.XAU}")

        return repository?.getMetalLatest() as LiveData<ResponseMetalApi>
    }
}