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
import com.example.fincalc.data.network.api_rates.ApiCurrency
import com.example.fincalc.data.network.api_rates.Rates
import com.example.fincalc.data.network.firebase.*
import com.example.fincalc.data.network.hasNetwork
import com.example.fincalc.models.cur_met.getRatesFromMap
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.HashMap


@Suppress("UNCHECKED_CAST")
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


    //Firestore + Api
    //Currency
    private val curRatesLiveData = MutableLiveData<RatesUi>()

    fun getLatestCurRates(): MutableLiveData<RatesUi> {

        CoroutineScope(Main).launch {
            val nowDate = Calendar.getInstance().time

            //Network ok
            if (hasNetwork(context)) {

                val resFromFire = FirestoreApi.getLatestCurRatesFire().await()
                val docSnapshot = resFromFire.firstOrNull()
                val fireDateTime = docSnapshot?.getTime()
                Log.d("ksaks", "fireDateTime: $fireDateTime")

                // db is initialized
                fireDateTime?.let {

                    val duration = duration(fireDateTime, nowDate)

                    //newer rates
                    if (duration < 180) {
                        val ratesMap = docSnapshot.get(CUR_RATES) as HashMap<String, Double>
                        val rates: Rates = getRatesFromMap(ratesMap)
                        Log.d("ksaks", "FIRE<180: ${rates.AMD}")
                        curRatesLiveData.value = RatesUi(fireDateTime, rates)
                    } else {  //catch new rates
                        try {
                            val response = ApiCurrency(context).getLatestRates().await()
                            val rates = response.rates
                            Log.d("ksaks", "API >180: ${rates.AMD}")
                            curRatesLiveData.value = RatesUi(nowDate, rates)
                            FirestoreApi.setLatestCurRatesFire(rates)
                        } catch (ex: Exception) {
                            //there is a problem with source, get CACHED from Firestore
                            try {
                                val fireCache = FirestoreApi.getLatCurFromCache().await()
                                val ratesMap = fireCache[CUR_RATES] as HashMap<String, Double>
                                val rates: Rates = getRatesFromMap(ratesMap)
                                Log.d("ksaks", "PROBLEM API FIRE CACHE: ${rates.AMD}")
                                val ratesUi = RatesUi(fireCache.getTime(), rates, API_SOURCE_PROBLEM)
                                curRatesLiveData.value = ratesUi
                            } catch (ex: Exception) {
                                //fireStore CACHE error
                                val ratesUi = RatesUi(null, null, API_SOURCE_PROBLEM )
                                curRatesLiveData.value = ratesUi
                            }
                        }
                    }
                } ?: try { //init
                    val response = ApiCurrency(context).getLatestRates().await()
                    val rates = response.rates
                    Log.d("ksaks", "INIT: ${rates.AMD}")
                    curRatesLiveData.value = RatesUi(nowDate, rates)
                    FirestoreApi.setLatestCurRatesFire(rates)
                } catch (ex: Exception) {
                    val ratesUi = RatesUi(null, null, API_SOURCE_PROBLEM )
                    curRatesLiveData.value = ratesUi
                }
            } else {  //NO Network - Firestore Cache
                try {
                    val fireCache = FirestoreApi.getLatCurFromCache().await()
                    val ratesMap = fireCache[CUR_RATES] as HashMap<String, Double>
                    val rates: Rates = getRatesFromMap(ratesMap)
                    Log.d("ksaks", "FIRE CACHE NO NETWORK: ${rates.AMD}")
                    val ratesUi = RatesUi(fireCache.getTime(), rates, NO_NETWORK)
                    curRatesLiveData.value = ratesUi
                } catch (ex: Exception) {
                    //fireStore CACHE error
                    val ratesUi = RatesUi(null, null, NO_NETWORK )
                    curRatesLiveData.value = ratesUi
                }
            }
        }
        return curRatesLiveData
    }

    fun getHisCurRates(date: String): MutableLiveData<RatesUi> {

        CoroutineScope(Main).launch {

            //Network ok
            if (hasNetwork(context)) {

                //1. get from firestore collections
                val docSnapshot = FirestoreApi.getHisCurRatesFireL(date)
                if (docSnapshot != null) {
                    val ratesMap = docSnapshot[CUR_RATES] as HashMap<String, Double>
                    val rates: Rates = getRatesFromMap(ratesMap)
                    Log.d("ksaks", "FIRE RATE CUR AMD: ${rates.AMD}")
                    val ratesUi = RatesUi(null, rates)
                    curRatesLiveData.value = ratesUi

                } else {
                    Log.d("ksaks", "ELSE - API")
                    //2. get from retrofit, add to firestore hist collection
                    try {
                        val response = ApiCurrency(context).getHistoricalRates(date).await()
                        val rates = response.rates
                        Log.d("ksaks", "API RATE AMD: ${rates.AMD}")
                        curRatesLiveData.value = RatesUi(null, rates)
                        FirestoreApi.setHisCurRatesFire(date, rates)
                    } catch (exc: Exception) {
                        //problem to fetch api, get another date please
                        Log.d("ksaks", "API FETCH PROBLEM")
                        val ratesUi = RatesUi(null, null, API_SOURCE_PROBLEM )
                        curRatesLiveData.value = ratesUi
                    }
                }
            } else {  //no network
                try {
                    val fireCache = FirestoreApi.getHisCurFromCache(date)
                    Log.d("ksaks", "NO NETWORK fireCache: $fireCache")

                    val ratesMap = fireCache!![CUR_RATES] as HashMap<String, Double>
                    val rates: Rates = getRatesFromMap(ratesMap)
                    Log.d("ksaks", "NO NETWORK CACHE RATE AMD: ${rates.AMD}")
                    val ratesUi = RatesUi(fireCache.getTime(), rates, NO_NETWORK)
                    curRatesLiveData.value = ratesUi
                } catch (ex: Exception) {
                    Log.d("ksaks", "NO NETWORK Outer Exception: ${ex.message}")
                    try {
                        val fireCache2 = FirestoreApi.getHisCurFromCache2(date)
                        Log.d("ksaks", "NO NETWORK fireCache: $fireCache2")
                        val ratesMap = fireCache2!![CUR_RATES] as HashMap<String, Double>
                        val rates: Rates = getRatesFromMap(ratesMap)
                        Log.d("ksaks", "NO NETWORK CACHE RATE AMD: ${rates.AMD}")
                        val ratesUi = RatesUi(fireCache2.getTime(), rates, NO_NETWORK)
                        curRatesLiveData.value = ratesUi
                    } catch (ex: Exception) {
                        //fireStore CACHE error
                        Log.d("ksaks", "FIRE CACHE PROBLEM")
                        Log.d("ksaks", "FIRE CACHE PROBLEM: ${ex.message}")
                        val ratesUi = RatesUi(null, null, NO_NETWORK )
                        curRatesLiveData.value = ratesUi
                    }
                }

            }
        }
        return curRatesLiveData
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