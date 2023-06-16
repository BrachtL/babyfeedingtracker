package com.example.babyfeedingtrackermvvm.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

open class BaseRepository(val context: Context) {

    fun isConnectionAvailable(): Boolean {

        val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
        val activeNet = connectionManager.activeNetwork ?: return false
        val networkCapabilities = connectionManager.getNetworkCapabilities(activeNet)
            ?: return false

        return (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

}