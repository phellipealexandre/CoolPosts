package com.phellipesilva.coolposts.state

import android.content.Context
import android.net.ConnectivityManager
import dagger.Reusable
import javax.inject.Inject

@Reusable
class ConnectionManager @Inject constructor(private val context: Context) {

    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected
    }
}