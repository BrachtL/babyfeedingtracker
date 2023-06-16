package com.example.babyfeedingtrackermvvm.model

import com.google.gson.annotations.SerializedName

data class UserModel(

    //@SerializedName("token")
    //lateinit var token: String

    @SerializedName("username")
    val username: String,

    @SerializedName("station")
    val station: String,

    @SerializedName("userColorClient")
    val color: String

)

