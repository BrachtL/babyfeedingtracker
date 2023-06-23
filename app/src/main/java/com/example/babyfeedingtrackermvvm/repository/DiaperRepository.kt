package com.example.babyfeedingtrackermvvm.repository

import android.content.Context
import android.util.Log
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.model.DiaperDataResponse
import com.example.babyfeedingtrackermvvm.service.DiaperService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaperRepository (context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(DiaperService::class.java)

    fun setDiaperChangeTimestamp(username: String, station: String, listener: APIListener<DiaperDataResponse>) {
        if(!isConnectionAvailable()) {
            listener.onFailure(context.getString(R.string.check_internet_connection))
            return
        }
        Log.d("DiaperRepository", "setDiaperTimestamp: Username: $username, Station: $station")
        executeCallT(remote.setDiaperChangeTimestamp(username, station), listener)
    }


    fun getDiaperData(username: String, station: String, listener: APIListener<DiaperDataResponse>) {
        if(!isConnectionAvailable()) {
            listener.onFailure(context.getString(R.string.check_internet_connection))
            return
        }

        executeCallT(remote.getDiaperData(username, station), listener)
    }
}