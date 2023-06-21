package com.example.babyfeedingtrackermvvm.service

import com.example.babyfeedingtrackermvvm.model.FeedingDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedingService {

    @GET("/getFeedingScreenData")
    fun getFeedingData(
        @Query("username") username: String,
        @Query("station") station: String
    ) : Call<FeedingDataResponse>
}