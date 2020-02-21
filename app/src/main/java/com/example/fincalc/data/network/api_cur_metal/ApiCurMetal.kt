package com.example.fincalc.data.network.api_cur_metal

import android.content.Context
import com.example.fincalc.data.network.createOkHttpClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.currencyscoop.com/v1/"
private const val API_KEY = "0bba3f2c5f3083ba3623938dbf492052"
//https://api.currencyscoop.com/v1/historical?api_key=0bba3f2c5f3083ba3623938dbf492052&date=2020-01-12
//https://api.currencyscoop.com/v1/latest?api_key=0bba3f2c5f3083ba3623938dbf492052
//Base USD

const val HEADER_CACHE_CONTROL = "Cache-Control-CurMet"
const val HEADER = "CurMet"
private const val ACCESS_KEY = "api_key"
private const val CACHE_SIZE = 5 * 1024 * 1024.toLong()


interface ApiCurMetal {

    @GET("latest")
    fun getLatestRatesAsync(): Deferred<ResponseCurMetal>

    @GET("historical")
    fun getHistoricalRatesAsync(
        @Query("date") date: String
    ): Deferred<ResponseCurMetal>


    @Suppress("DEPRECATION")
    companion object {
        operator fun invoke(context: Context): ApiCurMetal {

            return Retrofit.Builder()
                .client(
                    createOkHttpClient(
                        context,
                        HEADER,
                        HEADER_CACHE_CONTROL,
                        ACCESS_KEY,
                        API_KEY,
                        CACHE_SIZE
                    )
                )
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build()
                .create(ApiCurMetal::class.java)
        }
    }
}

