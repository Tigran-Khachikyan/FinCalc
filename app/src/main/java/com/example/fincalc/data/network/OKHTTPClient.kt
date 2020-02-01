package com.example.fincalc.data.network

import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


fun createOkHttpClient(
    context: Context,
    header: String,
    cacheContrl: String,
    name: String,
    API_KEY: String,
    cacheSize: Long
): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(requestInterceptor(name, API_KEY))
    .cache(cache(context, cacheSize))
    .addInterceptor(httpLoggingInterceptor())
    .addNetworkInterceptor(networkInterceptor(header, cacheContrl))
    .addInterceptor(offlineInterceptor(context, header, cacheContrl))
    .build()

private fun cache(context: Context, cacheSize: Long): Cache? {
    return Cache(context.cacheDir, cacheSize)
}

private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor =
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
        })
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return httpLoggingInterceptor
}

private fun requestInterceptor(name: String, API_KEY: String): Interceptor {
    return Interceptor { chain ->

        val url = chain.request()
            .url()
            .newBuilder()
            .addQueryParameter(name, API_KEY)
            .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        return@Interceptor chain.proceed(request)
    }
}

private fun networkInterceptor(header: String, cacheContrl: String): Interceptor {

    return Interceptor { chain ->
        val response: Response? = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(30, TimeUnit.MINUTES)
            .build()
        response?.newBuilder()
            ?.removeHeader(header)
            ?.removeHeader(cacheContrl)
            ?.header(cacheContrl, cacheControl.toString())
            ?.build()
    }
}

private fun offlineInterceptor(context: Context, header: String, cacheContrl: String): Interceptor {
    return Interceptor { chain ->
        Log.d("ggg", "offline interceptor: called.")

        var request: Request = chain.request()
        if (!hasNetwork(context)) {
            val cacheControl = CacheControl.Builder()
                .maxStale(30, TimeUnit.DAYS)
                .build()
            request = request.newBuilder()
                .removeHeader(header)
                .removeHeader(cacheContrl)
                .cacheControl(cacheControl)
                .build()
        }
        chain.proceed(request)
    }
}