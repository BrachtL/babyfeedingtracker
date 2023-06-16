package com.example.babyfeedingtrackermvvm.repository

import android.content.Context
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

class UserRepository(context: Context) /* : BaseRepository(context) */ {
    // TODO: add a BaseRepository class for code that could be shared between Repositories

    private val remote = RetrofitClient.getService(UserService::class.java)

    fun register(username: String, station: String, color: String, listener: APIListener<String>) {
        if(/*TODO: isConnectionAvailable method in BaseRepository */ !true) {
            listener.onFailure("Por favor, verifique se há conexão com a internet")
            return
        }
        val userModel = UserModel(
        username, station, color)

        executeCall(remote.register(userModel), listener)

    }

    // TODO: mandar essas 2 funções para a BaseRepository, já que as outras provavelmente usarão este código
    fun executeCall(call: Call<APIGeneralResponse>, listener: APIListener<String>) {
        call.enqueue(object : Callback<APIGeneralResponse> {
            override fun onResponse(call: Call<APIGeneralResponse>, response: Response<APIGeneralResponse>) {
                if (response.code() == 200) {
                    // TODO: futuramente trocar na API o código para outro número no caso de bad user
                    response.body()?.let {
                        if (it.message == "userIsNowOwner" || it.message == "userIsNowPending") {
                            listener.onSuccess(it.message)
                        } else {
                            listener.onFailure(it.message)
                        }
                    } ?: run {
                        listener.onFailure("Empty response body")
                    }
                } else {
                    listener.onFailure("Failed with status code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<APIGeneralResponse>, t: Throwable) {
                listener.onFailure("Ocorreu um erro, tente novamente mais tarde")
            }
        })
    }

    /*
    private fun failResponse(str: String): String {
        return Gson().fromJson(str, String::class.java)
    }
    */



}