package com.example.babyfeedingtrackermvvm.repository

import android.content.Context
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.model.UserModel
import com.example.babyfeedingtrackermvvm.service.UserService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserRepository(context: Context) /* : BaseRepository(context) */ {
    // TODO: add a BaseRepository class for code that could be shared between Repositories

    private val remote = RetrofitClient.getService(UserService::class.java)

    fun register(username: String, station: String, color: String, listener: APIListener<UserModel>) {
        //por quê existe esse listener?
        if(/*TODO: isConnectionAvailable method in BaseRepository */ !true) {
            listener.onFailure("Por favor, verifique se há conexão com a internet")
            return
        }
        val userModel = UserModel()
        userModel.username = username
        userModel.station = station
        userModel.color = color

        executeCall(remote.register(userModel), listener)


    }

    // TODO: mandar essas 2 funções para a BaseRepository, já que as outras provavelmente usarão este código
    fun <T> executeCall(call: Call<T>, listener: APIListener<T>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.code() == 200) {
                    response.body()?.let { listener.onSuccess(it) }
                    //listener.onSuccess(failResponse(response.body().toString()))
                } else {
                    listener.onFailure(failResponse(response.errorBody()!!.string()))
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                listener.onFailure("Ocorreu um erro, tente novamente mais tarde")
            }
        })
    }

    private fun failResponse(str: String): String {
        return Gson().fromJson(str, String::class.java)
    }



}