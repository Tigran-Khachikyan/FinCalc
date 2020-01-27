package com.example.fincalc.data.network.firebase

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.fincalc.data.network.api_rates.Rates
import com.example.fincalc.data.network.api_rates.ResponseCurApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val CANCEL = "cancel_777"
private const val CURRENCY = "Currency_Rates"
private const val METAL = "Metal_Rates"

object FirebaseApi {

    private val cancelRate = ResponseCurApi(CANCEL, "", Rates(), false, 0)

    private val fireDb = FirebaseDatabase.getInstance()
    private val currencyReference = fireDb.getReference(CURRENCY)

    private val latCurRates = MutableLiveData<ResponseCurApi>()


    fun getLatestCurRatesFire(): MutableLiveData<ResponseCurApi> {
        CoroutineScope(IO).launch {
            var rates: ResponseCurApi? = null
            async {
                currencyReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    @SuppressLint("SimpleDateFormat")
                    override fun onDataChange(p0: DataSnapshot) {

                        val lastKey = p0.children.last().key
                        val serverDateTime = lastKey?.getDateWithServerTimeStamp()
                        serverDateTime?.let {

                            val nowDate = Calendar.getInstance().time
                            val duration = duration(serverDateTime, nowDate)
                            Log.d("huysuhavat", " NOW: $nowDate, SERVER_TIME: $serverDateTime ")

                            rates = if (duration < 120) {
                                Log.d("huysuhavat", " duration: $duration")

                                p0.children.last().getValue(ResponseCurApi::class.java)
                            } else {
                                Log.d("huysuhavat", " duration>120 AMD value")
                                cancelRate
                            }

                            Log.d("huysuhavat", " OnDatChange Rate: ${rates?.date}")

                            latCurRates.value = rates
                        } ?: latCurRates.setValue(cancelRate)
                    }
                })
            }.await()
            return@launch withContext(Main) {
                latCurRates.value = rates
            }
        }
        return latCurRates
    }


    fun setLatestCurRatesFire(rates: ResponseCurApi?) {
        rates?.let {
            val now = Calendar.getInstance().time
            val nowString = now.getStringTimeStampWithDate()
            currencyReference.child(nowString).setValue(rates)
            Log.d("huysuhavat", "FIREBASE_API,  SET NEW RATE: ${rates.rates.AMD}")

        }
    }

}





