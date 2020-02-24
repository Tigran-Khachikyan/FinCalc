package com.my_1st.fincalc.data.network.api_crypto

import android.content.Context
import com.my_1st.fincalc.data.network.createOkHttpClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "http://api.coinlayer.com/api/"
private const val API_KEY = "13ca0b892321570f52913123dfba48df"
//http://api.coinlayer.com/api/live?access_key=13ca0b892321570f52913123dfba48df

private const val HEADER_CACHE_CONTROL = "Cache-Control-Crypto"
private const val HEADER = "Crypto"
private const val ACCESS_KEY = "access_key"
private const val CACHE_SIZE = 5 * 1024 * 1024.toLong()


interface ApiCrypto {

    @GET("live")
    fun getLatestRatesAsync(): Deferred<ResponseCrypto>

    @GET("{data}")
    fun getHistoricalRatesAsync(
        @Path(value = "data", encoded = false) data: String
    ): Deferred<ResponseCrypto>

    companion object {
        operator fun invoke(context: Context): ApiCrypto {

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
                .create(ApiCrypto::class.java)
        }
    }
}

