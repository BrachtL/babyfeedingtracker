package com.example.babyfeedingtrackermvvm.model

import com.google.gson.annotations.SerializedName

class UserModel {

    //@SerializedName("token")
    //lateinit var token: String

    @SerializedName("username")
    lateinit var username: String

    @SerializedName("station")
    lateinit var station: String

    @SerializedName("userColorClient")
    lateinit var color: String

}

