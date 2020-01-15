package com.example.fincalc.data.network.api_metals


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://metals-api.com/api/"
private const val API_KEY = "70fcq5s7ms8h1ak1jlsbbn8a48bv7g2791xkbn5r8zdklhbv59qjuhgds7ck0hr8"
//http://metals-api.com/api/2019-09-24?access_key=70fcq5s7ms8h1ak1jlsbbn8a48bv7g2791xkbn5r8zdklhbv59qjuhgds7ck0hr8

const val HEADER_CACHE_CONTROL = "Cache-Control"
const val HEADER_PRAGMA = "Pragma"
private const val cacheSize = 5 * 1024 * 1024.toLong()


interface ApiMetal {

    @GET("latest")
    fun getLatestMetal(): Call<ResponseMetalApi>

    @GET("{data}")
    fun getHistoricalMetal(
        @Path(
            value = "data",
            encoded = false
        ) data: String
    ): Call<ResponseMetalApi>


    @Suppress("DEPRECATION")
    companion object {
        operator fun invoke(context: Context): ApiMetal {

            return Retrofit.Builder()
                .client(okHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build()
                .create(ApiMetal::class.java)
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
                if (!hasNetwork(context)!!) {
                    val cacheControl = CacheControl.Builder()
                        .maxStale(120, TimeUnit.SECONDS)
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
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                    //    Log.d("ggg", message.toString())
                })
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return httpLoggingInterceptor
        }

        private fun hasNetwork(context: Context): Boolean? {
            var isConnected: Boolean? = false // Initial Value
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
        }
    }
}

