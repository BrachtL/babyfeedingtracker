package com.example.babyfeedingtrackermvvm

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    //instantiate repositories

    //instantiate MutableLiveData and LiveData to be observed
    private val _loginMessage = MutableLiveData<String>()
    val loginMessage: LiveData<String> = _loginMessage

    fun doLogin(username: String, station: String, color: String) {
        if(!validateLoginField(username, 3, 7, " '`|#$*/-,;()\\" + "\n" + '"')) {
            _loginMessage.value = "Usuário deve conter entre 3 e 7 caracteres não especiais"
            return
        }
        if(!validateLoginField(station, 6, 30, " '`|#$*/-,;()\\" + "\n" + '"')) {
            _loginMessage.value = "Station deve conter entre 6 e 30 caracteres não especiais"
            return
        }
        if(color == "") {
            _loginMessage.value = "Por favor selecione uma cor"
            return
        }

        _loginMessage.value = "success"

    }

    fun verifyIfLogged(/* some variables */) {

    }

    private fun validateLoginField(userInput: String, minLength: Int, maxLength: Int, forbiddenChars: String): Boolean {

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