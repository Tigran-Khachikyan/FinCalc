package com.example.fincalc.data.network.api_currency

import android.content.Context
import com.example.fincalc.data.network.createOkHttpClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "http://data.fixer.io/api/"
private const val API_KEY = "2a5eee9c2d53241e72d0c9c4b65d0019"
//http://data.fixer.io/api/2020-01-12?access_key=2a5eee9c2d53241e72d0c9c4b65d0019

private const val HEADER_CACHE_CONTROL = "Cache-Control-Cur"
private const val HEADER = "Cur"
private const val ACCESS_KEY = "access_key"
private const val CACHE_SIZE = 5 * 1024 * 1024.toLong()


interface ApiCurrency {

    @GET("latest")
    fun getLatestRates(): Deferred<ResponseCurrency>

    @GET("{data}")
    fun getHistoricalRates(
        @Path(
            value = "data",
            encoded = false
        ) data: String
    ): Deferred<ResponseCurrency>


    @Suppress("DEPRECATION")
    companion object {
        operator fun invoke(context: Context): ApiCurrency {

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
                .create(ApiCurrency::class.java)
        }
    }
}

