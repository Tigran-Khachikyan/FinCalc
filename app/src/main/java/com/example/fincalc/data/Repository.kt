package com.example.fincalc.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fincalc.data.db.Database
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.data.network.Rates
import com.example.fincalc.data.network.api_crypto.ApiCrypto
import com.example.fincalc.data.network.api_cur_metal.ApiCurMetal
import com.example.fincalc.data.network.firebase.*
import com.example.fincalc.data.network.firebase.RatesType.*
import com.example.fincalc.data.network.hasNetwork
import com.example.fincalc.models.rates.getCryptoRatesFromMap
import com.example.fincalc.models.rates.getRatesFromMap
import com.example.fincalc.ui.formatterCalendar
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*


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

    //FireStore + Api
    private val curLiveData = MutableLiveData<RatesFull>()
    private val cryptoLiveData = MutableLiveData<RatesFull>()

    fun getLatestCur(): LiveData<RatesFull> {
        CoroutineScope(Main).launch {
            curLiveData.value = getLatestRates(CURRENCY)
        }
        return curLiveData
    }

    fun getHistoricCur(date: String): LiveData<RatesFull> {
        CoroutineScope(Main).launch {
            curLiveData.value = getHistoricRates(date, CURRENCY)
        }
        return curLiveData
    }

    fun getLatestCrypto(): LiveData<RatesFull> {
        CoroutineScope(Main).launch {
            cryptoLiveData.value = getLatestRates(CRYPTO)
        }
        return cryptoLiveData
    }

    fun getHistoricCrypto(date: String): LiveData<RatesFull> {
        CoroutineScope(Main).launch {
            cryptoLiveData.value = getHistoricRates(date, CRYPTO)
        }
        return cryptoLiveData
    }

    private suspend fun getLatestRates(type: RatesType): RatesFull {
        val nowDate = Calendar.getInstance().time
        //Network ok
        return if (hasNetwork(context)) {
            val docSnapshot = FireStoreApi.getLatestRatesFire(type)

            // db is initialized
            if (docSnapshot != null && !docSnapshot.isEmpty) {

                val latestDoc = docSnapshot.last()
                val base = latestDoc.get(BASE) as String?
                val elderDoc = docSnapshot.firstOrNull()
                Log.d("derdd", "REPO elderDoc: $elderDoc")
                val latestDateTime = latestDoc.getTime()!!
                val duration = duration(latestDateTime, nowDate)

                //newer rates
                if (duration < 180) {
                    val latRates = getRatesFromSnapshot(latestDoc, type)!!
                    val elderRates = getRatesFromSnapshot(elderDoc, type)
                    Log.d("derdd", "REPO elderRates<180: $elderRates")

                    RatesFull(latestDateTime, latRates, elderRates, base)
                } else {  //catch new rates
                    try {
                        val baseApi: String?
                        val curRates = when (type) {
                            CURRENCY -> {
                                val response = ApiCurMetal(context).getLatestRates().await()
                                baseApi = response.response.base
                                response.response.rates
                            }
                            CRYPTO -> {
                                val response = ApiCrypto(context).getLatestRates().await()
                                baseApi = response.base
                                response.rates
                            }
                            else -> TODO()
                        }
                        val elderRates = getRatesFromSnapshot(latestDoc, type)
                        Log.d("derdd", "REPO elderRates>180 Api: $elderRates")

                        FireStoreApi.setLatestRatesFire(curRates, baseApi)
                        RatesFull(nowDate, curRates, elderRates, baseApi)
                    } catch (ex: Exception) {
                        //there is a problem with source, get CACHED from Firestore
                        try {
                            val latRates = getRatesFromSnapshot(latestDoc, type)!!
                            val elderRates = getRatesFromSnapshot(elderDoc, type)
                            Log.d("derdd", "REPO elderRates no Api: $elderRates")

                            RatesFull(
                                latestDateTime, latRates, elderRates, base, API_SOURCE_PROBLEM
                            )
                        } catch (ex: Exception) {
                            //fireStore CACHE error
                            RatesFull(null, null, null, null, API_SOURCE_PROBLEM)
                        }
                    }
                }
            } else //init
                try {
                    val baseApi: String?
                    val curRates = when (type) {
                        CURRENCY -> {
                            val response = ApiCurMetal(context).getLatestRates().await()
                            baseApi = response.response.base
                            response.response.rates
                        }
                        CRYPTO -> {
                            val response = ApiCrypto(context).getLatestRates().await()
                            baseApi = response.base
                            response.rates
                        }
                        else -> TODO()
                    }
                    FireStoreApi.setLatestRatesFire(curRates, baseApi)
                    RatesFull(nowDate, curRates, null, baseApi)
                } catch (ex: Exception) {
                    RatesFull(null, null, null, null, API_SOURCE_PROBLEM)
                }
        } else {  //NO Network - Firestore Cache
            try {
                val fireCache = FireStoreApi.getLatestRatesFromCacheAsync(type)?.await()
                val base = fireCache?.let { it.get(BASE) as String? }
                val ratesCached = getRatesFromSnapshot(fireCache, type)!!
                //fireCache?.getTime()?.let { formatterLong.format(fireCache.getTime()) }
                RatesFull(fireCache?.getTime(), ratesCached, null, base, NO_NETWORK)
            } catch (ex: Exception) {
                //fireStore CACHE error
                RatesFull(null, null, null, null, NO_NETWORK)
            }
        }
    }

    private suspend fun getHistoricRates(date: String, type: RatesType): RatesFull {

        //Network ok
        return if (hasNetwork(context)) {

            //1. get from firestore collections
            val docSnapshot = FireStoreApi.getHisCurRatesFireL(date, type)
            if (docSnapshot != null) {
                val rates = getRatesFromSnapshot(docSnapshot, type)!!
                val base = docSnapshot.get(BASE) as String?
                val hisDate = docSnapshot.get(DATE) as String?
                val dateFrom = hisDate?.let { formatterCalendar.parse(hisDate) }
                RatesFull(dateFrom, rates, null, base)

            } else {
                //2. get from retrofit, add to firestore hist collection
                try {
                    val baseApi: String?
                    val hisRates = when (type) {
                        CURRENCY -> {
                            val response = ApiCurMetal(context).getHistoricalRates(date).await()
                            baseApi = response.response.base
                            response.response.rates
                        }
                        CRYPTO -> {
                            val response = ApiCrypto(context).getHistoricalRates(date).await()
                            baseApi = response.base
                            response.rates
                        }
                        else -> TODO()
                    }
                    FireStoreApi.setHisRatesFire(date, hisRates, baseApi)
                    val dateFrom = formatterCalendar.parse(date)
                    RatesFull(dateFrom, hisRates, null, baseApi)
                } catch (exc: Exception) {
                    //problem to fetch api, get another date please
                    RatesFull(null, null, null, null, API_SOURCE_PROBLEM)
                }
            }
        } else {  //no network
            try {
                val fireHisCollCache = FireStoreApi.getHisRatesFromHisCollCache(date, type)
                val base = fireHisCollCache?.let { it.get(BASE) as String? }
                val hisDate = fireHisCollCache?.let { it.get(DATE) as String? }
                val dateFrom = hisDate?.let { formatterCalendar.parse(hisDate) }
                val rates = getRatesFromSnapshot(fireHisCollCache, type)!!
                RatesFull(dateFrom, rates, null, base, NO_NETWORK)
            } catch (ex: Exception) {
                try {
                    val fireLatCollCache = FireStoreApi.getHisRatesFromLatCollCache(date, type)
                    val rates = getRatesFromSnapshot(fireLatCollCache, type)!!
                    val base = fireLatCollCache?.let { it.get(BASE) as String? }
                    val hisDate = fireLatCollCache?.let { it.get(DATE) as String? }
                    val dateFrom = hisDate?.let { formatterCalendar.parse(hisDate) }
                    RatesFull(dateFrom, rates, null, base, NO_NETWORK)
                } catch (ex: Exception) {
                    //fireStore CACHE error
                    RatesFull(null, null, null, null, NO_NETWORK)
                }
            }
        }
    }

    private fun getRatesFromSnapshot(snapshot: DocumentSnapshot?, type: RatesType): Rates? {

        val ratesMap = snapshot?.let { snapshot.get(RATES) as HashMap<String, Double> }
        return when (type) {
            CURRENCY -> ratesMap?.let { getRatesFromMap(ratesMap) }
            CRYPTO -> ratesMap?.let { getCryptoRatesFromMap(ratesMap) }
            else -> TODO()
        }
    }

    /* private fun getBaseFromSnapshot(snapshot: DocumentSnapshot?) =
         snapshot?.let { snapshot.get(BASE) as String? }*/

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