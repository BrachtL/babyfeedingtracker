package com.example.babyfeedingtrackermvvm.service

import com.example.babyfeedingtrackermvvm.model.APIGeneralResponse
import com.example.babyfeedingtrackermvvm.model.UserModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    // TODO: trocar nome dos recursos na API e criar um register e um login
    @POST("station/checkAvailabilityAndPost")
    fun register(
        @Body user: UserModel
    ) : Call<APIGeneralResponse>
}