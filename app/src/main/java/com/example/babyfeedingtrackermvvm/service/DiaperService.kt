package com.example.babyfeedingtrackermvvm.service

import com.example.babyfeedingtrackermvvm.model.DiaperDataResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DiaperService {

    @GET("/getDiaperTimerDurationAndLastUsername")
    //@GET("/getDiaperTimerDurationAndLastUsernameMOCK") // MOCK
    fun getDiaperData(
        @Query("username") username: String,
        @Query("station") station: String
    ) : Call<DiaperDataResponse>

    @FormUrlEncoded
    @POST("/setDiaperChangeTime")
    //@POST("/setDiaperChangeTimeMOCK") // MOCK
    fun setDiaperChangeTimestamp(
        @Field("username") username: String,
        @Field("station") station: String
    ) : Call<DiaperDataResponse>

}