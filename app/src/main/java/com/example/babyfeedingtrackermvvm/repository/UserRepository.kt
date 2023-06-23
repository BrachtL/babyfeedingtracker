package com.example.babyfeedingtrackermvvm.repository

import android.content.Context
import android.util.Log
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.model.APIGeneralResponse
import com.example.babyfeedingtrackermvvm.model.UserModel
import com.example.babyfeedingtrackermvvm.service.UserService
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: change on API the status code for username already in use and pending
class UserRepository(context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(UserService::class.java)

    fun register(username: String, station: String, color: String, listener: APIListener<APIGeneralResponse>) {
        if(!isConnectionAvailable()) {
            listener.onFailure(context.getString(R.string.check_internet_connection))
            return
        }
        val userModel = UserModel(
        username, station, color)

        executeCallT(remote.register(userModel), listener)
    }

    /*
    private fun failResponse(str: String): String {
        return Gson().fromJson(str, String::class.java)
    }
    */

}