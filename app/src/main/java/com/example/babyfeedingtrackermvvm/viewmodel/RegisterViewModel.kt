package com.example.babyfeedingtrackermvvm.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.model.UserModel
import com.example.babyfeedingtrackermvvm.repository.RetrofitClient
import com.example.babyfeedingtrackermvvm.repository.UserPreferences
import com.example.babyfeedingtrackermvvm.repository.UserRepository

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    //instantiate repositories
    val userPreferences = UserPreferences(application.applicationContext)
    private val userRepository = UserRepository(application.applicationContext)

    //instantiate MutableLiveData and LiveData to be observed
    private val _registerMessage = MutableLiveData<String>()
    val registerMessage: LiveData<String> = _registerMessage

    fun doRegister(username: String, station: String, color: String) {
        if(!validateRegisterField(username, 3, 7, " '`|#$*/-,;()\\" + "\n" + '"')) {
            _registerMessage.value = "Usuário deve conter entre 3 e 7 caracteres não especiais"
            return
        }
        if(!validateRegisterField(station, 6, 30, " '`|#$*/-,;()\\" + "\n" + '"')) {
            _registerMessage.value = "Station deve conter entre 6 e 30 caracteres não especiais"
            return
        }
        if(color == "") {
            _registerMessage.value = "Por favor selecione uma cor"
            return
        }

        userRepository.register(username, station, color, object : APIListener<UserModel> {
            override fun onSuccess(result: UserModel) {
                //userPreferences.store("token", result.token)
                userPreferences.store("username", username)
                userPreferences.store("station", station)
                userPreferences.store("userColor", color)

                //RetrofitClient.addToken(result.token)


                _registerMessage.value = "username: " + username +  "station: " + station + "color: " + color
                //_registerMessage.value = "success"
            }

            override fun onFailure(message: String) {
                TODO("Not yet implemented")
            }

        })

    }

    fun verifyIfLogged(/* some variables */) {

    }

    private fun validateRegisterField(userInput: String, minLength: Int, maxLength: Int, forbiddenChars: String): Boolean {

        var isForbiddenChar = false

        if (forbiddenChars != "") {
            outerLoop@ for (char in userInput) {
                for (forbiddenChar in forbiddenChars) {
                    if (char == forbiddenChar) {
                        isForbiddenChar = true
                        break@outerLoop
                    }
                }
            }
        }

        return !(userInput.length < minLength || userInput.length > maxLength || isForbiddenChar)
    }

}