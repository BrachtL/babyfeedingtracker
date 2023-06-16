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

class UserRepository(context: Context) : BaseRepository(context) {

    private val remote = RetrofitClient.getService(UserService::class.java)

    fun register(username: String, station: String, color: String, listener: APIListener<String>) {
        if(!isConnectionAvailable()) {
            listener.onFailure(context.getString(R.string.check_internet_connection))
            // TODO: Remove HARDCODED
            return
        }
        val userModel = UserModel(
        username, station, color)

        executeCall(remote.register(userModel), listener)

    }

    fun executeCall(call: Call<APIGeneralResponse>, listener: APIListener<String>) {
        call.enqueue(object : Callback<APIGeneralResponse> {
            override fun onResponse(call: Call<APIGeneralResponse>, response: Response<APIGeneralResponse>) {
                if (response.code() == 200) {
                    // TODO: change on API the status code for username already in use and pending
                    response.body()?.let {
                        if (it.message == "userIsNowOwner" || it.message == "userIsNowPending") {
                            listener.onSuccess(it.message)
                        } else {
                            listener.onFailure(it.message)
                        }
                    } ?: run {
                        listener.onFailure(context.getString(R.string.empty_response_body))
                    }
                } else {
                    listener.onFailure(context.getString(R.string.failed_status_code, response.code()))
                }
            }

            override fun onFailure(call: Call<APIGeneralResponse>, t: Throwable) {
                listener.onFailure(context.getString(R.string.error_try_again))
                Log.d("TOO MUCH TIME?", "onFailure: $t")
            }
        })
    }

    /*
    private fun failResponse(str: String): String {
        return Gson().fromJson(str, String::class.java)
    }
    */

}