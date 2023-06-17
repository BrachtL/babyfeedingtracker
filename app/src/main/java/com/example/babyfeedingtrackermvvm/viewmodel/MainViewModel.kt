package com.example.babyfeedingtrackermvvm.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.repository.DiaperRepository
import com.example.babyfeedingtrackermvvm.repository.UserPreferences

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    //instantiate repositories
    private val userPreferences = UserPreferences(application.applicationContext)
    private val diaperRepository = DiaperRepository(application.applicationContext)

    //instantiate MutableLiveData and LiveData to be observed
    private val _timerText = MutableLiveData<Long>()
    val timerText : LiveData<Long> = _timerText

    private val _isLoginValid = MutableLiveData<Boolean>()
    val isLoginValid : LiveData<Boolean> = _isLoginValid

    private var username: String = ""
    private var station: String = ""
    //private var color: String = ""

    var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())

    fun getDiaperData() {
        // trocar o valor de _timerText.value em algum momento

        diaperRepository.getDiaperData(username, station, object : APIListener<Long> {
            override fun onSuccess(result: Long) {
                if(result > 0) {
                    _timerText.value = result
                    startTimer(result)

                } else {
                    // TODO: fazer lógica da notificação
                }

                // TODO: fazer lógica do valor menor que zero
                // TODO: fazer lógica do "timer" para ir atualizando 1x/segundo no front
            }

            override fun onFailure(message: String) {
                // TODO: toast error "Erro tentando obter o tempo restante para a troca de fraldas"
            }

        })
    }

    fun startTimer(initialValue: Long) {
        if (isTimerRunning) {
            // Timer is already running, no need to start another one
            return
        }

        isTimerRunning = true
        updateTimerValue(initialValue)
    }

    private fun updateTimerValue(value: Long) {
        if(isTimerRunning) {
            _timerText.value = value

            if (value > 0) {
                handler.postDelayed({
                    updateTimerValue(value - 1000)
                }, 1000)
            } else {
                stopTimer()
                // TODO: Make a new request to the API or perform other necessary tasks
            }
        }
    }

    fun stopTimer() {
        isTimerRunning = false
        //handler.removeCallbacksAndMessages(null)
    }


    // TODO: it needs to be tested
    fun loadUserData() {
        username = userPreferences.get("username")
        station = userPreferences.get("station")
        //color = userPreferences.get("userColor")

        Log.d("User Logado é", "loadUserData: $username, $station")

        if(username == "" || station == "") {
            _isLoginValid.value = false
        }
    }


}