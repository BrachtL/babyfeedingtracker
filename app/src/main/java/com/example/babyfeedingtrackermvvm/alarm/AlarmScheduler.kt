package com.example.babyfeedingtrackermvvm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmScheduler(private val alarmManager: AlarmManager, val context: Context) :
    AlarmSchedulerInterface {

    override fun scheduleAlarm(alarmTime: Long) {
        val pendingIntent = createPendingIntent()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + alarmTime,
            pendingIntent
        )
    }

    override fun cancelAlarm() {
        val pendingIntent = createPendingIntent()
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        // Set any extra data or actions if needed

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE, // Provide a unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val REQUEST_CODE = 1

        // TODO: study and fix it (memory leak because of the context as param?)
        private lateinit var INSTANCE: AlarmScheduler

        fun getAlarmInstance(alarmManager: AlarmManager, context: Context): AlarmScheduler {

            if (!::INSTANCE.isInitialized) {
                synchronized(AlarmScheduler::class) {
                    INSTANCE = AlarmScheduler(alarmManager, context)
                }
            }
            return INSTANCE
        }

    }
}