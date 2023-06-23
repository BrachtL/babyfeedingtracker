package com.example.babyfeedingtrackermvvm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.model.APIGeneralResponse
import com.example.babyfeedingtrackermvvm.repository.UserPreferences
import com.example.babyfeedingtrackermvvm.repository.UserRepository

class RegisterViewModel(private val application: Application) : AndroidViewModel(application) {

    //instantiate repositories
    val userPreferences = UserPreferences(application.applicationContext)
    private val userRepository = UserRepository(application.applicationContext)

    //instantiate MutableLiveData and LiveData to be observed
    private val _registerMessage = MutableLiveData<String>()
    val registerMessage: LiveData<String> = _registerMessage

    fun doRegister(username: String, station: String, color: String) {
        if(!validateRegisterField(username, 3, 7, " '`|#$*/-,;()\\" + "\n" + '"')) {
            _registerMessage.value = application.applicationContext.getString(R.string.user_name_restrictions)
            return
        }
        if(!validateRegisterField(station, 6, 30, " '`|#$*/-,;()\\" + "\n" + '"')) {
            _registerMessage.value = application.applicationContext.getString(R.string.station_name_restrictions)
            return
        }
        if(color == "") {
            _registerMessage.value = application.applicationContext.getString(R.string.choose_color)
            return
        }

        userRepository.register(username, station, color, object : APIListener<APIGeneralResponse> {
            override fun onSuccess(result: APIGeneralResponse) {

                //when create PendingActivity, remove this "userIsNowPending" case from here
                // TODO: remove this hardcoded from my username as soon as I create the JWT login in API
                if (result.message == "userIsNowOwner" || result.message == "userIsNowPending") {
                    //userPreferences.store("token", result.token)
                    userPreferences.store("username", username)
                    userPreferences.store("station", station)
                    userPreferences.store("userColor", color)

                    //RetrofitClient.addToken(result.token)

                    _registerMessage.value = result.message
                } else if(username == "Luciano" || username == "Marceli") {

                    userPreferences.store("username", username)
                    userPreferences.store("station", station)
                    userPreferences.store("userColor", color)

                    _registerMessage.value = "userIsNowOwner"
                } else {
                    _registerMessage.value = result.message
                }
            }

            override fun onFailure(message: String) {
                _registerMessage.value = message
            }
        })
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