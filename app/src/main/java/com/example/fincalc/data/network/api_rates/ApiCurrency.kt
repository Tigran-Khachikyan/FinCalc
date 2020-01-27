package com.example.fincalc.data.network.api_rates

import android.content.Context
import android.util.Log
import com.example.fincalc.data.network.hasNetwork
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

private const val BASE_URL = "22http://data.fixer.io/api/"
private const val API_KEY = "2a5eee9c2d53241e72d0c9c4b65d0019"
//http://data.fixer.io/api/2020-01-12?access_key=2a5eee9c2d53241e72d0c9c4b65d0019

const val HEADER_CACHE_CONTROL = "Cache-Control-Cur"
const val HEADER_PRAGMA = "Cur"
private const val cacheSize = 5 * 1024 * 1024.toLong()


interface ApiCurrency {

    @GET("latest")
    fun getLatestRates(): Deferred<ResponseCurApi>

    @GET("{data}")
    fun getHistoricalRates(
        @Path(
            value = "data",
            encoded = false
        ) data: String
    ): Deferred<ResponseCurApi>


    @Suppress("DEPRECATION")
    companion object {
        operator fun invoke(context: Context): ApiCurrency {

            return Retrofit.Builder()
                .client(okHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build()
                .create(ApiCurrency::class.java)
        }

        private fun okHttpClient(context: Context): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor())
            .cache(cache(context))
            .addInterceptor(httpLoggingInterceptor())
            .addNetworkInterceptor(networkInterceptor())
            .addInterceptor(offlineInterceptor(context))
            .build()

        private fun cache(context: Context): Cache? {
            return Cache(context.cacheDir, cacheSize)
        }


        private fun offlineInterceptor(context: Context): Interceptor {
            return Interceptor { chain ->
                Log.d("ggg", "offline interceptor: called.")

                var request: Request = chain.request()
                if (!hasNetwork(context)) {
                    val cacheControl = CacheControl.Builder()
                        .maxStale(60, TimeUnit.SECONDS)
                        .build()
                    request = request.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build()
                }
                chain.proceed(request)
            }
        }

        private fun networkInterceptor(): Interceptor {

            return Interceptor { chain ->
                Log.d("ggg", "network interceptor: called.")

                val response: Response? = chain.proceed(chain.request())
                val cacheControl = CacheControl.Builder()
                    .maxAge(30, TimeUnit.SECONDS)
                    .build()
                response?.newBuilder()
                    ?.removeHeader(HEADER_PRAGMA)
                    ?.removeHeader(HEADER_CACHE_CONTROL)
                    ?.header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    ?.build()
            }
        }


        private fun requestInterceptor(): Interceptor {
            return Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("access_key", API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)
            }
        }


        private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                    //    Log.d("ggg", message.toString())
                })
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return httpLoggingInterceptor
        }


    }
}

