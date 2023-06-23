package com.example.babyfeedingtrackermvvm.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.listener.APIListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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


    fun <T> executeCallT(call: Call<T>, listener: APIListener<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.code() == 200) {
                    response.body()?.let {
                        listener.onSuccess(it)
                    } ?: run {
                        listener.onFailure(context.getString(R.string.empty_response_body))
                    }
                } else {
                    listener.onFailure(context.getString(R.string.failed_status_code, response.code()))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                //I am not using the call.message here. I should use when refactor API.

                listener.onFailure(context.getString(R.string.error_try_again))
                Log.d("executeCall", "onFailure: $t")
            }
        })
    }

}