package com.example.babyfeedingtrackermvvm.repository

import android.content.Context
import android.util.Log
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.model.APIGeneralResponse
import com.example.babyfeedingtrackermvvm.model.FeedingDataRequest
import com.example.babyfeedingtrackermvvm.model.FeedingDataResponse
import com.example.babyfeedingtrackermvvm.service.FeedingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedingRepository(context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(FeedingService::class.java)

    fun setFeeding(feedingData: FeedingDataRequest, listener: APIListener<APIGeneralResponse>) {
        if(!isConnectionAvailable()) {
            listener.onFailure(context.getString(R.string.check_internet_connection))
            return
        }

        executeCallT(remote.setFeedingData(feedingData), listener)
    }

    fun getFeedingData(username: String, station: String, listener: APIListener<FeedingDataResponse>) {
        if(!isConnectionAvailable()) {
            listener.onFailure(context.getString(R.string.check_internet_connection))
            return
        }

        executeCallT(remote.getFeedingData(username, station), listener)
    }
}