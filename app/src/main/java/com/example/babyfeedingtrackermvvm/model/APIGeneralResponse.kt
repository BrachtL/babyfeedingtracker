package com.example.babyfeedingtrackermvvm.model

import com.google.gson.annotations.SerializedName

data class APIGeneralResponse(
    @SerializedName("message")
    val message: String
)
