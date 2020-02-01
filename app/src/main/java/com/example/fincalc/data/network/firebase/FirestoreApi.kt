package com.example.fincalc.data.network.firebase

import android.util.Log
import com.example.fincalc.data.network.api_rates.RatesCurrency
import com.example.fincalc.models.cur_met_crypto.getMapFromCurRates
import com.google.firebase.firestore.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred
import java.io.Serializable
import java.util.*
import kotlin.collections.HashMap


private const val CURRENCY_LATEST = "Currency_Latest"
private const val CURRENCY_HISTORICAL = "Currency_Historical"
private const val METAL = "Metal Rates"
private const val CACHE = "Cached_Rates"
const val DATE = "Short-Date"
const val DATE_TIME = "Long_Date"
const val CUR_RATES = "Currency_Rates"
const val NO_NETWORK = 123
const val API_SOURCE_PROBLEM = 255
const val OK = 777

object FirestoreApi {

    val firestoreDb by lazy {
        FirebaseFirestore.getInstance()
    }


    fun setLatestCurRatesFire(rates: RatesCurrency?) {

        val nowDate = Calendar.getInstance().time
        val nowString = nowDate.getStringTimeStampWithDate()
        val nowShortDate = nowString.split(' ')[0]
        Log.d("ksaks", "nowShortDate: $nowShortDate")

        val ratesMap = rates?.let { getMapFromCurRates(rates) }
        val resultMap = ratesMap?.let {
            hashMapOf(
                DATE to nowShortDate,
                DATE_TIME to nowString,
                CUR_RATES to ratesMap
            )
        }
        writeToFirestor(CURRENCY_LATEST, nowString, resultMap)
        writeToFirestor(CURRENCY_LATEST, CACHE, resultMap)
    }

    fun setHisCurRatesFire(date: String, rates: RatesCurrency?) {

        val ratesMap = rates?.let { getMapFromCurRates(rates) }
        val resultMap = ratesMap?.let {
            hashMapOf(
                DATE to date,
                CUR_RATES to ratesMap
            )
        }
        writeToFirestor(CURRENCY_HISTORICAL, date, resultMap)
    }

    suspend fun getLatestCurRatesFire(): QuerySnapshot? {
        val def = firestoreDb.collection(CURRENCY_LATEST)
            .orderBy(DATE_TIME, Query.Direction.ASCENDING).limitToLast(3).get().asDeferred()
        return def.await()
    }

    suspend fun getHisCurRatesFireL(date: String): QueryDocumentSnapshot? {
        Log.d("ksaks", "inside method")

        //from FireStore Current Collection
        val defCurCol = firestoreDb.collection(CURRENCY_LATEST).whereEqualTo(DATE, date)
            .get().asDeferred()
        val result = defCurCol.await().lastOrNull()

        return if (result == null) {
            //from FireStore Historical Collection
            val defHisCol =
                firestoreDb.collection(CURRENCY_HISTORICAL).whereEqualTo(DATE, date).get()
                    .asDeferred()
            defHisCol.await().firstOrNull()
        } else result
    }

    fun getLatCurFromCache(): Deferred<DocumentSnapshot> {
        return firestoreDb.collection(CURRENCY_LATEST).document(CACHE).get(Source.CACHE)
            .asDeferred()
    }

    suspend fun getHisCurFromHisCollCache(date: String): DocumentSnapshot? {
        val def = firestoreDb.collection(CURRENCY_HISTORICAL).document(date)
            .get(Source.CACHE).asDeferred()
        val fromHisCache = def.await()
        Log.d("ksaks", "fromHisCache $fromHisCache")

        return fromHisCache
    }

    suspend fun getHisCurFromLatCollCache(date: String): DocumentSnapshot? {

        val defCurCol = firestoreDb.collection(CURRENCY_LATEST)
            .whereEqualTo(DATE, date).get(Source.CACHE).asDeferred()
        val fromCurCache = defCurCol.await().lastOrNull()
        Log.d("ksaks", "fromCurCache $fromCurCache")

        return fromCurCache
    }

    private fun writeToFirestor(
        collection: String, time: String, map: HashMap<String, Serializable>?
    ) {
        map?.let {
            firestoreDb.collection(collection)
                .document(time)
                .set(map)
                .addOnCompleteListener {
                    Log.d("ksaks", "ADDED to FIRESTORE")
                }
                .addOnFailureListener {
                    Log.d("ksaks", "FAILURE ADD: ${it.message}")
                }
        }
    }


}