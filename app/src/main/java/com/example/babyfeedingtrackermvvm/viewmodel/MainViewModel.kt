package com.example.babyfeedingtrackermvvm.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.babyfeedingtrackermvvm.Alarm.AlarmScheduler
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.repository.DiaperRepository
import com.example.babyfeedingtrackermvvm.repository.UserPreferences
import com.example.babyfeedingtrackermvvm.view.MainActivity

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler.getAlarmInstance(application.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager,
        application.applicationContext)

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
                    _timerText.value = 10000 // TODO: change this value, created for debugging purposes, to result
                    startTimer(10000) // TODO: change this value, created for debugging purposes, to result
                    alarmScheduler.scheduleAlarm(10000)


                } else {
                    // TODO: fazer lógica da notificação
                }

                // TODO: fazer lógica do AlarmManager
                // TODO: fazer lógica do "timer" virar um contador progressivo em vermelho
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