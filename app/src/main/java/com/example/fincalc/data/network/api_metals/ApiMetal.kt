package com.example.fincalc.data.network.api_metals


import android.content.Context
import com.example.fincalc.data.network.createOkHttpClient
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "http://metals-api.com/api/"
private const val API_KEY = "70fcq5s7ms8h1ak1jlsbbn8a48bv7g2791xkbn5r8zdklhbv59qjuhgds7ck0hr8"
//http://metals-api.com/api/2019-09-24?access_key=70fcq5s7ms8h1ak1jlsbbn8a48bv7g2791xkbn5r8zdklhbv59qjuhgds7ck0hr8

const val HEADER_CACHE_CONTROL = "Cache-Control-Metal"
const val HEADER = "Metal"
private const val ACCESS_KEY = "access_key"
private const val CACHE_SIZE = 5 * 1024 * 1024.toLong()


interface ApiMetal {

    @GET("latest")
    fun getLatestMetal(): Call<ResponseMetals>

    @GET("{data}")
    fun getHistoricalMetal(
        @Path(
            value = "data",
            encoded = false
        ) data: String
    ): Call<ResponseMetals>


    @Suppress("DEPRECATION")
    companion object {
        operator fun invoke(context: Context): ApiMetal {

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
                .create(ApiMetal::class.java)
        }
    }
}

