package com.example.babyfeedingtrackermvvm.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.content.Context.ALARM_SERVICE
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.alarm.AlarmScheduler
import com.example.babyfeedingtrackermvvm.alarm.DiaperChangeNotificationManager
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.repository.DiaperRepository
import com.example.babyfeedingtrackermvvm.repository.UserPreferences

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler.getAlarmInstance(application.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager,
        application.applicationContext)

    //instantiate repositories
    private val userPreferences = UserPreferences(application.applicationContext)
    private val diaperRepository = DiaperRepository(application.applicationContext)

    //instantiate MutableLiveData and LiveData to be observed
    private val _timerText = MutableLiveData<Long>()
    val timerText : LiveData<Long> = _timerText

    private val _failureMessage = MutableLiveData<String>()
    val failureMessage: LiveData<String> = _failureMessage

    private val _timerTextColor = MutableLiveData<Int>()
    val timerTextColor: LiveData<Int> = _timerTextColor

    private val _isLoginValid = MutableLiveData<Boolean>()
    val isLoginValid : LiveData<Boolean> = _isLoginValid

    private var username: String = ""
    private var station: String = ""
    //private var color: String = ""

    var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())

    fun getDiaperData() {
        Toast.makeText(application.applicationContext, "getDiaperData() was called", Toast.LENGTH_SHORT).show()
        // trocar o valor de _timerText.value em algum momento

        diaperRepository.getDiaperData(username, station, object : APIListener<Long> {
            override fun onSuccess(result: Long) {
                if(result > 0) {
                    _timerTextColor.value = android.R.color.holo_blue_dark
                    _timerText.value = result
                    startTimer(result, -1000)
                    alarmScheduler.scheduleAlarm(result)
                    // TODO: atualizar a imagem com a fralda


                } else {
                    _timerTextColor.value = R.color.red
                    startTimer(-result, 1000)
                    _timerText.value = result
                    // TODO: atualizar a imagem com o cocô
                    alarmScheduler.cancelAlarm()
                    DiaperChangeNotificationManager().notifyDiaperChange(application.applicationContext)
                }

            }

            override fun onFailure(message: String) {
                _failureMessage.value = message
            }

        })
    }

    fun startTimer(initialValue: Long, ticker: Long) {
        if (isTimerRunning) {
            isTimerRunning = false
            handler.removeCallbacksAndMessages(null)
        }

        isTimerRunning = true
        updateTimerValue(initialValue, ticker)
    }

    private fun updateTimerValue(value: Long, ticker: Long) {
        if(isTimerRunning) {
            _timerText.value = value

            if (value > 0) {
                handler.postDelayed({
                    updateTimerValue(value + ticker, ticker)
                }, 1000)
            } else {
                if(value == 0L && _timerTextColor.value == android.R.color.holo_blue_dark) {
                    stopTimer()
                    getDiaperData()
                } else { // TODO: testar remover esse código, acho que nunca vai cair aqui. Trocar o value == 0L para value <= 0
                    handler.postDelayed({
                        updateTimerValue(value + ticker, ticker)
                    }, 1000)
                }

            }
        }
    }

    fun stopTimer() {
        isTimerRunning = false
        handler.removeCallbacksAndMessages(null)
    }


    // TODO depois: it needs to be tested
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