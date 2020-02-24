@file:Suppress("DEPRECATION")

package com.my_1st.fincalc.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

fun hasNetwork(context: Context): Boolean {

    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}