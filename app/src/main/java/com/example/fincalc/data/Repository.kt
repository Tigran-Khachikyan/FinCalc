package com.example.fincalc.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fincalc.data.db.Database
import com.example.fincalc.data.db.dep.Deposit
import com.example.fincalc.data.db.loan.Loan
import com.example.fincalc.data.network.Rates
import com.example.fincalc.data.network.api_crypto.ApiCrypto
import com.example.fincalc.data.network.api_rates.ApiCurrency
import com.example.fincalc.data.network.firebase.*
import com.example.fincalc.data.network.firebase.RatesType.*
import com.example.fincalc.data.network.hasNetwork
import com.example.fincalc.models.rates.getCryptoRatesFromMap
import com.example.fincalc.models.rates.getCurRatesFromMap
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
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
    private val ratesLiveData = MutableLiveData<RatesFull>()

    fun getLatestRates(type: RatesType): MutableLiveData<RatesFull> {

        CoroutineScope(Main).launch {
            val nowDate = Calendar.getInstance().time

            //Network ok
            if (hasNetwork(context)) {
                val docSnapshot = FireStoreApi.getLatestRatesFire(type)

                // db is initialized
                if (docSnapshot != null && !docSnapshot.isEmpty) {
                    val latestDoc = docSnapshot.last()

                    val elderDoc = docSnapshot.firstOrNull()
                    val latestDateTime = latestDoc.getTime()!!
                    val duration = duration(latestDateTime, nowDate)

                    //newer rates
                    if (duration < 180) {
                        val latRates = getRatesFromSnapshot(latestDoc, type)!!
                        val elderRates = getRatesFromSnapshot(elderDoc, type)
                        ratesLiveData.value = RatesFull(latestDateTime, latRates, elderRates)
                    } else {  //catch new rates
                        try {
                            val curRates = when (type) {
                                CURRENCY -> ApiCurrency(context).getLatestRates().await().rates
                                CRYPTO -> ApiCrypto(context).getLatestRates().await().rates
                                else -> TODO()
                            }
                            val elderRates = getRatesFromSnapshot(latestDoc, type)
                            ratesLiveData.value = RatesFull(nowDate, curRates, elderRates)
                            FireStoreApi.setLatestRatesFire(curRates)
                        } catch (ex: Exception) {
                            //there is a problem with source, get CACHED from Firestore
                            try {
                                val latRates = getRatesFromSnapshot(latestDoc, type)!!
                                val elderRates = getRatesFromSnapshot(elderDoc, type)
                                ratesLiveData.value = RatesFull(
                                    latestDateTime, latRates, elderRates, API_SOURCE_PROBLEM
                                )
                            } catch (ex: Exception) {
                                //fireStore CACHE error
                                val ratesUi = RatesFull(null, null, null, API_SOURCE_PROBLEM)
                                ratesLiveData.value = ratesUi
                            }
                        }
                    }
                } else try { //init
                    val curRates = when (type) {
                        CURRENCY -> ApiCurrency(context).getLatestRates().await().rates
                        CRYPTO -> ApiCrypto(context).getLatestRates().await().rates
                        else -> TODO()
                    }
                    ratesLiveData.value = RatesFull(nowDate, curRates, null)
                    FireStoreApi.setLatestRatesFire(curRates)

                } catch (ex: Exception) {
                    val ratesUi = RatesFull(null, null, null, API_SOURCE_PROBLEM)
                    ratesLiveData.value = ratesUi
                }
            } else {  //NO Network - Firestore Cache
                try {
                    val fireCache = FireStoreApi.getLatestRatesFromCacheAsync(type).await()
                    val ratesCached = getRatesFromSnapshot(fireCache, type)!!
                    val ratesUi = RatesFull(fireCache.getTime(), ratesCached, null, NO_NETWORK)
                    ratesLiveData.value = ratesUi
                } catch (ex: Exception) {
                    //fireStore CACHE error
                    val ratesUi = RatesFull(null, null, null, NO_NETWORK)
                    ratesLiveData.value = ratesUi
                }
            }
        }
        return ratesLiveData
    }

    fun getHistoricRates(date: String, type: RatesType): MutableLiveData<RatesFull> {

        CoroutineScope(Main).launch {
            //Network ok
            if (hasNetwork(context)) {

                //1. get from firestore collections
                val docSnapshot = FireStoreApi.getHisCurRatesFireL(date, type)
                if (docSnapshot != null) {
                    val rates = getRatesFromSnapshot(docSnapshot, type)!!
                    val ratesUi = RatesFull(null, rates, null)
                    ratesLiveData.value = ratesUi

                } else {
                    //2. get from retrofit, add to firestore hist collection
                    try {
                        val hisRates = when (type) {
                            CURRENCY -> ApiCurrency(context).getHistoricalRates(date).await().rates
                            CRYPTO -> ApiCrypto(context).getHistoricalRates(date).await().rates
                            else -> TODO()
                        }
                        ratesLiveData.value = RatesFull(null, hisRates, null)
                        FireStoreApi.setHisRatesFire(date, hisRates)
                    } catch (exc: Exception) {
                        //problem to fetch api, get another date please
                        val ratesUi = RatesFull(null, null, null, API_SOURCE_PROBLEM)
                        ratesLiveData.value = ratesUi
                    }
                }
            } else {  //no network
                try {
                    val fireHisCollCache = FireStoreApi.getHisRatesFromHisCollCache(date, type)
                    val rates = getRatesFromSnapshot(fireHisCollCache, type)!!
                    val ratesUi = RatesFull(null, rates, null, NO_NETWORK)
                    ratesLiveData.value = ratesUi
                } catch (ex: Exception) {
                    try {
                        val fireLatCollCache = FireStoreApi.getHisRatesFromLatCollCache(date, type)
                        // val ratesMap = fireLatCollCache!![RATES] as HashMap<String, Double>
                        val rates = getRatesFromSnapshot(fireLatCollCache, type)!!
                        val ratesUi = RatesFull(null, rates, null, NO_NETWORK)
                        ratesLiveData.value = ratesUi
                    } catch (ex: Exception) {
                        //fireStore CACHE error
                        val ratesUi = RatesFull(null, null, null, NO_NETWORK)
                        ratesLiveData.value = ratesUi
                    }
                }
            }
        }
        return ratesLiveData
    }

    private fun getRatesFromSnapshot(snapshot: DocumentSnapshot?, type: RatesType): Rates? {

        val ratesMap = snapshot?.let { snapshot.get(RATES) as HashMap<String, Double> }
        return when (type) {
            CURRENCY -> ratesMap?.let { getCurRatesFromMap(ratesMap) }
            CRYPTO -> ratesMap?.let { getCryptoRatesFromMap(ratesMap) }
            else -> TODO()
        }
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