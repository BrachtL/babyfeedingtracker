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

    fun getDiaperData(username: String, station: String, listener: APIListener<Long>) {
        if(!isConnectionAvailable()) {
            listener.onFailure(context.getString(R.string.check_internet_connection))
            return
        }

        executeCall(remote.getDiaperData(username, station), listener)

    }

    fun executeCall(call: Call<DiaperDataResponse>, listener: APIListener<Long>) {
        call.enqueue(object : Callback<DiaperDataResponse> {
            override fun onResponse(call: Call<DiaperDataResponse>, response: Response<DiaperDataResponse>) {
                if (response.code() == 200) {
                    response.body()?.let {
                        listener.onSuccess(it.timerDuration)
                        Log.d("Teste DIAPER TIMER DURATION", "onResponse: " + it)
                    } ?: run {
                        listener.onFailure(context.getString(R.string.empty_response_body))
                    }
                } else {
                    listener.onFailure(context.getString(R.string.failed_status_code, response.code()))
                }
            }

            override fun onFailure(call: Call<DiaperDataResponse>, t: Throwable) {
                listener.onFailure(context.getString(R.string.error_try_again))
                Log.d("TOO MUCH TIME?", "onFailure: $t")
            }
        })
    }

}