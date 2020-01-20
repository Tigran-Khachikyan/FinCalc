package com.example.fincalc.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fincalc.data.db.Database
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.data.network.api_metals.ApiMetal
import com.example.fincalc.data.network.api_metals.ResponseMetalApi
import com.example.fincalc.data.network.api_rates.ApiCurrency.Companion.invoke
import com.example.fincalc.data.network.api_rates.ResponseCurApi
import retrofit2.Call
import retrofit2.Callback

class Repository private constructor(
    private val context: Context
) {

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(context: Context): Repository? {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: Repository(context)
            }
        }
    }

    //Currency

    private val ratesLatest = MutableLiveData<ResponseCurApi>()
    private val ratesHistorical = MutableLiveData<ResponseCurApi>()

    fun getRatesLatest(): MutableLiveData<ResponseCurApi> {
        getLatestRates(context)
        Log.d("ggg", " result AMD in repo0: ${ratesLatest.value?.rates?.AMD}")

        return ratesLatest
    }

    fun getRatesHistorical(date: String): MutableLiveData<ResponseCurApi> {
        getHistoricalRates(context, date)
        return ratesHistorical
    }

    private fun getLatestRates(context: Context?) {
        invoke(context!!).getLatestRates()
            .enqueue(object : Callback<ResponseCurApi?> {
                override fun onResponse(
                    call: Call<ResponseCurApi?>,
                    response: retrofit2.Response<ResponseCurApi?>
                ) {
                    if (response.isSuccessful) {
                        ratesLatest.postValue(response.body())
                        Log.d("ggg", " result in Repo1: ${response.body()}")
                    }
                    Log.d("ggg", " result in Repo2: ${response.message()}")

                }

                override fun onFailure(
                    call: Call<ResponseCurApi?>,
                    t: Throwable
                ) {
                    Log.d("ggg", " result in failure: ${t.message}")

                }
            })
    }

    private fun getHistoricalRates(context: Context?, date: String) {
        invoke(context!!).getHistoricalRates(date)
            .enqueue(object : Callback<ResponseCurApi?> {
                override fun onResponse(
                    call: Call<ResponseCurApi?>,
                    response: retrofit2.Response<ResponseCurApi?>
                ) {
                    if (response.isSuccessful)
                        ratesHistorical.postValue(response.body())
                }

                override fun onFailure(
                    call: Call<ResponseCurApi?>,
                    t: Throwable
                ) {
                }
            })
    }


    //Metal

    private val metalsLatest = MutableLiveData<ResponseMetalApi>()
    private val metalsHistorical = MutableLiveData<ResponseMetalApi>()

    fun getMetalLatest(): MutableLiveData<ResponseMetalApi> {
        getLatestMetals(context)
        Log.d("ggg", " result AMD in repo0: ${metalsLatest.value?.rates?.XAU}")

        return metalsLatest
    }

    fun getMetalHistorical(date: String): MutableLiveData<ResponseMetalApi> {
        getHistoricalMetals(context, date)
        return metalsHistorical
    }

    private fun getLatestMetals(context: Context?) {
        ApiMetal.invoke(context!!).getLatestMetal()
            .enqueue(object : Callback<ResponseMetalApi?> {
                override fun onResponse(
                    call: Call<ResponseMetalApi?>,
                    response: retrofit2.Response<ResponseMetalApi?>
                ) {
                    if (response.isSuccessful) {
                        metalsLatest.postValue(response.body())
                        Log.d("ggg", " result in Repo1: ${response.body()}")
                    }
                    Log.d("ggg", " result in Repo2: ${response.message()}")

                }

                override fun onFailure(
                    call: Call<ResponseMetalApi?>,
                    t: Throwable
                ) {
                    Log.d("ggg", " result in failure: ${t.message}")

                }
            })
    }

    private fun getHistoricalMetals(context: Context?, date: String) {
        ApiMetal.invoke(context!!).getHistoricalMetal(date)
            .enqueue(object : Callback<ResponseMetalApi?> {
                override fun onResponse(
                    call: Call<ResponseMetalApi?>,
                    response: retrofit2.Response<ResponseMetalApi?>
                ) {
                    if (response.isSuccessful)
                        metalsHistorical.postValue(response.body())
                }

                override fun onFailure(
                    call: Call<ResponseMetalApi?>,
                    t: Throwable
                ) {
                }
            })
    }


    //Database
    //Loans
    fun getLoans(): LiveData<List<Loan>> =
        Database(context).getLoanDao().getLoans()

    suspend fun insertLoan(loan: Loan) =
        Database(context).getLoanDao().insert(loan)

    suspend fun deleteLoan(loan: Loan) =
        Database(context).getLoanDao().delete(loan)

    suspend fun deleteAllLoans() =
        Database(context).getLoanDao().deleteAll()


    //Deposit
    fun getDep(): LiveData<List<Deposit>> =
        Database(context).getDepDao().getDeposits()

    suspend fun insertDep(dep: Deposit) =
        Database(context).getDepDao().insert(dep)

    suspend fun deleteDep(dep: Deposit) =
        Database(context).getDepDao().delete(dep)

    suspend fun deleteAllDeps() =
        Database(context).getDepDao().deleteAll()

}