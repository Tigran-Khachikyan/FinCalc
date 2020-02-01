package com.example.fincalc.ui.cur.metals

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.fincalc.data.network.api_metals.ResponseMetals
import com.example.fincalc.data.Repository

@Suppress("UNCHECKED_CAST")
class MetalsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    fun getLatestMetals(): LiveData<ResponseMetals> {
        Log.d("ggg", " result AMD in ViewModel: ${repository?.getMetalLatest()?.value?.rates?.XAU}")

        return repository?.getMetalLatest() as LiveData<ResponseMetals>
    }
}