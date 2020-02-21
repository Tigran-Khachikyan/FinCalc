package com.example.fincalc.data.network.firebase

import com.example.fincalc.data.network.Rates
import com.example.fincalc.data.network.api_crypto.CryptoRates
import com.example.fincalc.data.network.api_cur_metal.CurMetRates
import com.example.fincalc.data.network.firebase.FBCollection.*
import com.example.fincalc.data.network.firebase.RatesType.CRYPTO
import com.example.fincalc.data.network.firebase.RatesType.CURRENCY
import com.example.fincalc.models.rates.getMapFromCryptoRates
import com.example.fincalc.models.rates.getMapFromRates
import com.google.firebase.firestore.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.tasks.asDeferred
import java.io.Serializable
import java.util.*

private const val CACHE = "Cached Rates"
const val DATE = "Short Date"
const val BASE = "Base"
const val DATE_TIME = "Long Date"
const val RATES = "Rates"
const val NO_NETWORK = 123
const val API_SOURCE_PROBLEM = 255
const val OK = 777

object FireStoreApi {
    private val fireStoreDb by lazy { FirebaseFirestore.getInstance() }

    fun setLatestRatesFire(rates: Rates?, base: String) {

        rates?.let {
            val nowDate = Calendar.getInstance().time
            val now = nowDate.getStringTimeStampWithDate()
            val nowShort = now.split(' ')[0]

            when (rates) {
                is CurMetRates -> {
                    val ratesMap = getMapFromRates(rates)
                    setToFBLatestColl(ratesMap, CURRENCY_LATEST, nowShort, now, base)
                }
                is CryptoRates -> {
                    val ratesMap = getMapFromCryptoRates(rates)
                    setToFBLatestColl(ratesMap, CRYPTO_LATEST, nowShort, now, base)
                }
            }
        }
    }

    private fun setToFBLatestColl(
        map: HashMap<String, Double>?,
        coll: FBCollection,
        nowShort: String,
        now: String,
        base: String
    ) {
        map?.let {
            val resultMap = hashMapOf(
                DATE to nowShort,
                DATE_TIME to now,
                BASE to base,
                RATES to map
            )
            writeToFireStore(coll, now, resultMap)
            writeToFireStore(coll, CACHE, resultMap)
        }
    }

    fun setHisRatesFire(date: String, rates: Rates?, base: String) {

        rates?.let {
            when (rates) {
                is CurMetRates -> setToFBHistoricColl(
                    getMapFromRates(rates), CURRENCY_HISTORICAL, date, base
                )
                is CryptoRates -> setToFBHistoricColl(
                    getMapFromCryptoRates(rates), CRYPTO_HISTORICAL, date, base
                )
            }
        }
    }

    private fun setToFBHistoricColl(
        map: HashMap<String, Double>?, coll: FBCollection, date: String, base: String
    ) {

        val resultMap = map?.let {
            hashMapOf(
                DATE to date,
                RATES to map,
                BASE to base
            )
        }
        writeToFireStore(coll, date, resultMap)
    }

    suspend fun getLatestRatesFire(type: RatesType): QuerySnapshot? {
        val def = when (type) {
            CURRENCY -> fireStoreDb.collection(CURRENCY_LATEST.name)
                .orderBy(DATE_TIME, Query.Direction.ASCENDING).limitToLast(3).get().asDeferred()
            CRYPTO -> fireStoreDb.collection(CRYPTO_LATEST.name)
                .orderBy(DATE_TIME, Query.Direction.ASCENDING).limitToLast(3).get().asDeferred()
        }
        return def.await()
    }

    suspend fun getHisCurRatesFireL(date: String, type: RatesType): QueryDocumentSnapshot? {

        return when (type) {
            CURRENCY -> {
                //from FireStore Current Collection
                val result = fireStoreDb.collection(CURRENCY_LATEST.name).whereEqualTo(DATE, date)
                    .get().asDeferred().await().lastOrNull()
                //from FireStore Historical Collection
                result ?: fireStoreDb.collection(CURRENCY_HISTORICAL.name).whereEqualTo(DATE, date)
                    .get().asDeferred().await().firstOrNull()
            }
            CRYPTO -> {
                val result = fireStoreDb.collection(CRYPTO_LATEST.name).whereEqualTo(DATE, date)
                    .get().asDeferred().await().lastOrNull()
                result ?: fireStoreDb.collection(CRYPTO_HISTORICAL.name).whereEqualTo(DATE, date)
                    .get().asDeferred().await().firstOrNull()
            }
        }
    }

    fun getLatestRatesFromCacheAsync(type: RatesType): Deferred<DocumentSnapshot>? {
        return when (type) {
            CURRENCY -> fireStoreDb.collection(CURRENCY_LATEST.name).document(CACHE)
                .get(Source.CACHE).asDeferred()
            CRYPTO -> fireStoreDb.collection(CRYPTO_LATEST.name).document(CACHE)
                .get(Source.CACHE).asDeferred()
        }
    }

    suspend fun getHisRatesFromHisCollCache(date: String, type: RatesType): DocumentSnapshot? {

        return when (type) {
            CURRENCY -> fireStoreDb.collection(CURRENCY_HISTORICAL.name).document(date)
                .get(Source.CACHE).asDeferred().await()
            CRYPTO -> fireStoreDb.collection(CRYPTO_HISTORICAL.name).document(date)
                .get(Source.CACHE).asDeferred().await()
        }
    }

    suspend fun getHisRatesFromLatCollCache(date: String, type: RatesType): DocumentSnapshot? {

        val deferredDocList = when (type) {
            CURRENCY -> fireStoreDb.collection(CURRENCY_LATEST.name)
                .whereEqualTo(DATE, date).get(Source.CACHE).asDeferred()
            CRYPTO -> fireStoreDb.collection(CURRENCY_LATEST.name)
                .whereEqualTo(DATE, date).get(Source.CACHE).asDeferred()
        }
        return deferredDocList.await().lastOrNull()
    }

    private fun writeToFireStore(
        coll: FBCollection, timeDocName: String, mapValue: HashMap<String, Serializable>?
    ) {
        mapValue?.let {
            fireStoreDb.collection(coll.name)
                .document(timeDocName)
                .set(mapValue)
                .addOnCompleteListener {}
                .addOnFailureListener {}
        }
    }
}