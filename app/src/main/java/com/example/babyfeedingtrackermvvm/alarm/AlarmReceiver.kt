package com.example.babyfeedingtrackermvvm.alarm

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.babyfeedingtrackermvvm.listener.APIListener
import com.example.babyfeedingtrackermvvm.repository.DiaperRepository
import com.example.babyfeedingtrackermvvm.repository.UserPreferences

// TODO: não notificar se já tiver uma notificação na tela?

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val alarmScheduler = AlarmScheduler.getAlarmInstance(context.getSystemService(
            Context.ALARM_SERVICE
        ) as AlarmManager,
            context)

        val userPreferences = UserPreferences(context)
        val username = userPreferences.get("username")
        val station = userPreferences.get("station")
        //color = userPreferences.get("userColor")

        Log.d("User Logado é", "loadUserData: $username, $station")

        if(username != "" && station != "") {
            val diaperRepository = DiaperRepository(context)

            diaperRepository.getDiaperData(username, station, object : APIListener<Long> {
                override fun onSuccess(result: Long) {
                    Log.d("RESULT", "RESULT do DB no AlarmReceiver: $result")
                    if(result > 0) {
                        alarmScheduler.scheduleAlarm(result)
                    } else {
                        DiaperChangeNotificationManager().notifyDiaperChange(context)
                        Log.d("Tentei notificar", "AlarmReceiver chamou DiaperChangeNotificationManager ")
                    }

                }

                override fun onFailure(message: String) {
                    // TODO: acredito que não faz nada também
                }
            })
        } else {
            // TODO: what to do if the user was not logged, for some reason, at this point? What I need to check before changing the activity?
        }

    }
}







