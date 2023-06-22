package com.example.babyfeedingtrackermvvm.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.content.Context.ALARM_SERVICE
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.alarm.AlarmScheduler
import com.example.babyfeedingtrackermvvm.alarm.DiaperChangeNotificationManager
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.model.APIGeneralResponse
import com.example.babyfeedingtrackermvvm.model.DiaperDataResponse
import com.example.babyfeedingtrackermvvm.model.FeedingDataRequest
import com.example.babyfeedingtrackermvvm.model.FeedingDataResponse
import com.example.babyfeedingtrackermvvm.repository.DiaperRepository
import com.example.babyfeedingtrackermvvm.repository.FeedingRepository
import com.example.babyfeedingtrackermvvm.repository.UserPreferences

class MainViewModel(private val application: Application) : AndroidViewModel(application) {

    private val alarmScheduler = AlarmScheduler.getAlarmInstance(application.applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager,
        application.applicationContext)

    //instantiate repositories
    private val userPreferences = UserPreferences(application.applicationContext)
    private val diaperRepository = DiaperRepository(application.applicationContext)
    private val feedingRepository = FeedingRepository(application.applicationContext)

    //instantiate MutableLiveData and LiveData to be observed
    private val _timerText = MutableLiveData<Long>()
    val timerText : LiveData<Long> = _timerText

    private val _failureMessage = MutableLiveData<String>()
    val failureMessage: LiveData<String> = _failureMessage

    private val _isLoginValid = MutableLiveData<Boolean>()
    val isLoginValid : LiveData<Boolean> = _isLoginValid

    private val _isDirty = MutableLiveData<Boolean>()
    val isDirty : LiveData<Boolean> = _isDirty

    private val _screenObject = MutableLiveData<FeedingDataResponse>()
    val screenObject: LiveData<FeedingDataResponse> = _screenObject

    private val _isBottleDisabled = MutableLiveData<Boolean>()
    val isBottleDisabled: LiveData<Boolean> = _isBottleDisabled

    private val _eraseAmount = MutableLiveData<Boolean>()
    val eraseAmount: LiveData<Boolean> = _eraseAmount

    private var username: String = ""
    private var station: String = ""
    //private var color: String = ""

    var isTimerRunning = false
    private val handler = Handler(Looper.getMainLooper())

    fun setFeeding(amount: Int) {
        _isBottleDisabled.value = true
        val feedingData = FeedingDataRequest(username, station, amount)
        feedingRepository.setFeeding(feedingData, object : APIListener<APIGeneralResponse> {
            override fun onSuccess(result: APIGeneralResponse) {
                _eraseAmount.value = true
                getFeedingData()
                // TODO: The API should send the new screen data, without needing to make a new request
            }

            override fun onFailure(message: String) {
                _isBottleDisabled.value = false
                _failureMessage.value = message
            }

        })
    }

    fun removeDiaperNotification() {
        // TODO: this isThereActiveNotification should be treated in the DiaperChangeNotificationManager()
        if(DiaperChangeNotificationManager().isThereActiveNotification(application.applicationContext)) {
            DiaperChangeNotificationManager().removeDiaperNotification(application.applicationContext)
        }
    }

    fun setDiaperTimestamp() {
        //Toast.makeText(application.applicationContext, "setDiaperData() was called", Toast.LENGTH_SHORT).show()
        Log.d("setDiaperTimestamp", "setDiaperTimestamp: Username: $username, Station: $station")

        diaperRepository.setDiaperChangeTimestamp(username, station, object : APIListener<DiaperDataResponse> {
            override fun onSuccess(result: DiaperDataResponse) {
                removeDiaperNotification()
                _timerText.value = result.timerDuration
                startTimer(result.timerDuration, -1000)
                alarmScheduler.scheduleAlarm(result.timerDuration)
                _isDirty.value = false
            }

            override fun onFailure(message: String) {
                _failureMessage.value = message
            }

        })
    }

    fun getFeedingData() {
        //Toast.makeText(application.applicationContext, "getFeedingData() was called", Toast.LENGTH_SHORT).show()
        _isBottleDisabled.value = true
        feedingRepository.getFeedingData(username, station, object : APIListener<FeedingDataResponse> {
            override fun onSuccess(result: FeedingDataResponse) {
                //Toast.makeText(application.applicationContext, "onSucces getFeedingData()", Toast.LENGTH_SHORT).show()
                Log.d("getFeedingData()", "onSuccess: $result")
                _isBottleDisabled.value = false
                _screenObject.value = result
            }

            override fun onFailure(message: String) {
                _isBottleDisabled.value = false
                _failureMessage.value = message
            }

        })
    }

    fun getDiaperData() {

        diaperRepository.getDiaperData(username, station, object : APIListener<DiaperDataResponse> {
            override fun onSuccess(result: DiaperDataResponse) {
                if(result.timerDuration > 0) {
                    _timerText.value = result.timerDuration
                    startTimer(result.timerDuration, -1000)
                    alarmScheduler.scheduleAlarm(result.timerDuration)
                    _isDirty.value = false
                    if(DiaperChangeNotificationManager().isThereActiveNotification(application.applicationContext)) {
                        DiaperChangeNotificationManager().removeDiaperNotification(application.applicationContext)
                    }

                } else {
                    startTimer(-result.timerDuration, 1000)
                    _timerText.value = result.timerDuration
                    _isDirty.value = true
                    alarmScheduler.cancelAlarm()
                    alarmScheduler.scheduleAlarm(900000L) //15 min
                    if(!DiaperChangeNotificationManager().isThereActiveNotification(application.applicationContext)) {
                        DiaperChangeNotificationManager().notifyDiaperChange(application.applicationContext)
                    }
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
                if(value == 0L && isDirty.value == false) {
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