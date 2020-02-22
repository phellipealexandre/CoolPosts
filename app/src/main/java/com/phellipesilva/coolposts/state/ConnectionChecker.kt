package com.phellipesilva.coolposts.state

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import javax.inject.Inject

class ConnectionChecker @Inject constructor(context: Context) {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isOnline(): Boolean =
        if (Build.VERSION.SDK_INT >= 23) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork).hasInternetConnection()
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnectedOrConnecting ?: false
        }

    private fun NetworkCapabilities?.hasInternetConnection() = this?.let {
        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || hasTransport(
            NetworkCapabilities.TRANSPORT_ETHERNET
        )
    } ?: false
}