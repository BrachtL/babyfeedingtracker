package com.example.babyfeedingtrackermvvm

import com.google.gson.annotations.SerializedName

class UserModel {

    @SerializedName("username")
    lateinit var username: String

    @SerializedName("station")
    lateinit var station: String

    @SerializedName("userColorClient")
    lateinit var color: String

}

