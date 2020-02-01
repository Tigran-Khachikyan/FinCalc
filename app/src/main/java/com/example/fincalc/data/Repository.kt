package com.example.fincalc.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fincalc.data.db.Database
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.data.network.api_metals.ApiMetal
import com.example.fincalc.data.network.api_metals.ResponseMetals
import com.example.fincalc.data.network.api_rates.ApiCurrency
import com.example.fincalc.data.network.api_rates.RatesCurrency
import com.example.fincalc.data.network.firebase.*
import com.example.fincalc.data.network.hasNetwork
import com.example.fincalc.models.cur_met_crypto.getRatesFromMap
import com.google.firebase.firestore.DocumentSnapshot
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

    private val metalsLatest = MutableLiveData<ResponseMetals>()
    private val metalsHistorical = MutableLiveData<ResponseMetals>()

    fun getMetalLatest(): MutableLiveData<ResponseMetals> {
        getLatestMetals(context)
        Log.d("ggg", " result AMD in repo0: ${metalsLatest.value?.rates?.XAU}")

        return metalsLatest
    }

    fun getMetalHistorical(date: String): MutableLiveData<ResponseMetals> {
        getHistoricalMetals(context, date)
        return metalsHistorical
    }

    private fun getLatestMetals(context: Context?) {
        ApiMetal.invoke(context!!).getLatestMetal()
            .enqueue(object : Callback<ResponseMetals?> {
                override fun onResponse(
                    call: Call<ResponseMetals?>,
                    response: retrofit2.Response<ResponseMetals?>
                ) {
                    if (response.isSuccessful) {
                        metalsLatest.postValue(response.body())
                        Log.d("ggg", " result in Repo1: ${response.body()}")
                    }
                    Log.d("ggg", " result in Repo2: ${response.message()}")

                }

                override fun onFailure(
                    call: Call<ResponseMetals?>,
                    t: Throwable
                ) {
                    Log.d("ggg", " result in failure: ${t.message}")
                }
            })
    }

    private fun getHistoricalMetals(context: Context?, date: String) {
        ApiMetal.invoke(context!!).getHistoricalMetal(date)
            .enqueue(object : Callback<ResponseMetals?> {
                override fun onResponse(
                    call: Call<ResponseMetals?>,
                    response: retrofit2.Response<ResponseMetals?>
                ) {
                    if (response.isSuccessful)
                        metalsHistorical.postValue(response.body())
                }

                override fun onFailure(
                    call: Call<ResponseMetals?>,
                    t: Throwable
                ) {
                }
            })
    }


    //Firestore + Api
    //Currency
    private val curRatesLiveData = MutableLiveData<RatesFull>()

    fun getLatestCurRates(): MutableLiveData<RatesFull> {

        CoroutineScope(Main).launch {
            val nowDate = Calendar.getInstance().time

            //Network ok
            if (hasNetwork(context)) {

                val docSnapshot = FirestoreApi.getLatestCurRatesFire()

                // db is initialized
                if (docSnapshot != null && !docSnapshot.isEmpty) {
                    val latestDoc = docSnapshot.last()

                    val elderDoc = docSnapshot.firstOrNull()
                    val latestDateTime = latestDoc.getTime()!!
                    Log.d("ksaks", "latestDateTime: $latestDateTime")
                    Log.d("ksaks", "oldersDateTime: ${elderDoc?.getTime()}")
                    Log.d("ksaks", "nowDateTime: $nowDate")
                    val duration = duration(latestDateTime, nowDate)

                    //newer rates
                    if (duration < 180) {
                        val latRates = getRatesFromSnapshot(latestDoc)!!
                        val elderRates = getRatesFromSnapshot(elderDoc)
                        Log.d("ksaks", "FIRE<180 LATEST AMD: ${latRates.AMD}")
                        Log.d("ksaks", "FIRE<180 ELDER AMD: ${elderRates?.AMD}")
                        curRatesLiveData.value = RatesFull(latestDateTime, latRates, elderRates)
                    } else {  //catch new rates
                        try {
                            val responseApi = ApiCurrency(context).getLatestRates().await()
                            val ratesApi = responseApi.rates
                            val elderRates = getRatesFromSnapshot(latestDoc)
                            curRatesLiveData.value = RatesFull(nowDate, ratesApi, elderRates)
                            Log.d("ksaks", "API>180 LATEST AMD: ${ratesApi.AMD}")
                            Log.d("ksaks", "FIRE>180 ELDER AMD: ${elderRates?.AMD}")
                            FirestoreApi.setLatestCurRatesFire(ratesApi)
                        } catch (ex: Exception) {
                            //there is a problem with source, get CACHED from Firestore
                            try {
                                val latRates = getRatesFromSnapshot(latestDoc)!!
                                val elderRates = getRatesFromSnapshot(elderDoc)
                                Log.d("ksaks", "FIRE>180 API PROBLEM LATEST AMD: ${latRates.AMD}")
                                Log.d("ksaks", "FIRE>180 API PROBLEM ELDER AMD: ${elderRates?.AMD}")
                                curRatesLiveData.value = RatesFull(
                                    latestDateTime, latRates, elderRates, API_SOURCE_PROBLEM
                                )
                            } catch (ex: Exception) {
                                //fireStore CACHE error
                                Log.d("ksaks", "NETWORK OK, FIRE & API PROBLEM ELDER AMD")
                                val ratesUi = RatesFull(null, null, null, API_SOURCE_PROBLEM)
                                curRatesLiveData.value = ratesUi
                            }
                        }
                    }
                } else try { //init
                    val responseApi = ApiCurrency(context).getLatestRates().await()
                    val ratesApi = responseApi.rates
                    Log.d("ksaks", "INIT: ${ratesApi.AMD}")
                    curRatesLiveData.value = RatesFull(nowDate, ratesApi, null)
                    FirestoreApi.setLatestCurRatesFire(ratesApi)
                } catch (ex: Exception) {
                    Log.d("ksaks", "INIT API PROBLEM")
                    val ratesUi = RatesFull(null, null, null, API_SOURCE_PROBLEM)
                    curRatesLiveData.value = ratesUi
                }
            } else {  //NO Network - Firestore Cache
                try {
                    val fireCache = FirestoreApi.getLatCurFromCache().await()
                    val ratesCached = getRatesFromSnapshot(fireCache)!!
                    Log.d("ksaks", "FIRE CACHE NO NETWORK: ${ratesCached.AMD}")
                    val ratesUi = RatesFull(fireCache.getTime(), ratesCached, null, NO_NETWORK)
                    curRatesLiveData.value = ratesUi
                } catch (ex: Exception) {
                    //fireStore CACHE error
                    Log.d("ksaks", "NETWORK NO, FIRE CACHE PROBLEM")
                    val ratesUi = RatesFull(null, null, null, NO_NETWORK)
                    curRatesLiveData.value = ratesUi
                }
            }
        }
        return curRatesLiveData
    }

    fun getHisCurRates(date: String): MutableLiveData<RatesFull> {

        Log.d("kraxx","DATE: $date")
        CoroutineScope(Main).launch {

            //Network ok
            if (hasNetwork(context)) {

                //1. get from firestore collections
                val docSnapshot = FirestoreApi.getHisCurRatesFireL(date)
                if (docSnapshot != null) {
                    val rates = getRatesFromSnapshot(docSnapshot)!!
                    Log.d("ksaks", "FIRE RATE CUR AMD: ${rates.AMD}")
                    val ratesUi = RatesFull(null, rates, null)
                    curRatesLiveData.value = ratesUi

                } else {
                    Log.d("ksaks", "ELSE - API")
                    //2. get from retrofit, add to firestore hist collection
                    try {
                        val response = ApiCurrency(context).getHistoricalRates(date).await()
                        val rates = response.rates
                        Log.d("ksaks", "API RATE AMD: ${rates.AMD}")
                        curRatesLiveData.value = RatesFull(null, rates, null)
                        FirestoreApi.setHisCurRatesFire(date, rates)
                    } catch (exc: Exception) {
                        //problem to fetch api, get another date please
                        Log.d("ksaks", "API FETCH PROBLEM")
                        val ratesUi = RatesFull(null, null, null, API_SOURCE_PROBLEM)
                        curRatesLiveData.value = ratesUi
                    }
                }
            } else {  //no network
                try {
                    val fireHisCollCache = FirestoreApi.getHisCurFromHisCollCache(date)
                    Log.d("ksaks", "NO NETWORK fireCache: $fireHisCollCache")

                    val rates = getRatesFromSnapshot(fireHisCollCache)!!
                    Log.d("ksaks", "NO NETWORK CACHE RATE AMD: ${rates.AMD}")
                    val ratesUi = RatesFull(null, rates, null, NO_NETWORK)
                    curRatesLiveData.value = ratesUi
                } catch (ex: Exception) {
                    Log.d("ksaks", "NO NETWORK Outer Exception: ${ex.message}")
                    try {
                        val fireLatCollCache = FirestoreApi.getHisCurFromLatCollCache(date)
                        Log.d("ksaks", "NO NETWORK fireCache: $fireLatCollCache")
                        val ratesMap = fireLatCollCache!![CUR_RATES] as HashMap<String, Double>
                        val rates = getRatesFromSnapshot(fireLatCollCache)!!
                        Log.d("ksaks", "NO NETWORK CACHE RATE AMD: ${rates.AMD}")
                        val ratesUi = RatesFull(null, rates, null, NO_NETWORK)
                        curRatesLiveData.value = ratesUi
                    } catch (ex: Exception) {
                        //fireStore CACHE error
                        Log.d("ksaks", "FIRE CACHE PROBLEM")
                        Log.d("ksaks", "FIRE CACHE PROBLEM: ${ex.message}")
                        val ratesUi = RatesFull(null, null, null, NO_NETWORK)
                        curRatesLiveData.value = ratesUi
                    }
                }

            }
        }
        return curRatesLiveData
    }

    private fun getRatesFromSnapshot(snapshot: DocumentSnapshot?): RatesCurrency? {

        val ratesMap = snapshot?.let { snapshot.get(CUR_RATES) as HashMap<String, Double> }
        return ratesMap?.let { getRatesFromMap(ratesMap) }
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