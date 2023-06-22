package com.example.babyfeedingtrackermvvm.model

import com.google.gson.annotations.SerializedName

data class FeedingDataRequest(

    @SerializedName("username")
    val username: String,

    @SerializedName("station")
    val station: String,

    @SerializedName("amount")
    val amount: Int
)
