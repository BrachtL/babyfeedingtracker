package com.example.babyfeedingtrackermvvm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmScheduler(
    private val alarmManager: AlarmManager
) : AlarmSchedulerInterface {

    //private val contextRef: WeakReference<Context> = WeakReference(context)

    override fun scheduleAlarm(alarmTimeMillis: Long, context: Context) {
        val pendingIntent = createPendingIntent(context)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + alarmTimeMillis,
            pendingIntent
        )
    }

    override fun cancelAlarm(context: Context) {
        val pendingIntent = createPendingIntent(context)
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)

        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE, // Provide a unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val REQUEST_CODE = 1

        private lateinit var INSTANCE: AlarmScheduler

        fun getAlarmInstance(alarmManager: AlarmManager): AlarmScheduler {
            synchronized(AlarmScheduler::class) {
                if (!::INSTANCE.isInitialized) {
                    INSTANCE = AlarmScheduler(alarmManager)
                }
            }
            return INSTANCE
        }
    }
}


/* Old Alarm Scheduler with memory leak because of passing context in a wrong way


class OldAlarmScheduler(private val alarmManager: AlarmManager, val context: Context) :
    AlarmSchedulerInterface {

    override fun scheduleAlarm(alarmTimeMillis: Long) {
        val pendingIntent = createPendingIntent()

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + alarmTimeMillis,
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
        const val REQUEST_CODE = 1

        // TODO: study and fix it (memory leak because of the context as param?)
        private lateinit var INSTANCE: OldAlarmScheduler

        fun getAlarmInstance(alarmManager: AlarmManager, context: Context): OldAlarmScheduler {

            if (!::INSTANCE.isInitialized) {
                synchronized(OldAlarmScheduler::class) {
                    INSTANCE = OldAlarmScheduler(alarmManager, context)
                }
            }
            return INSTANCE
        }

    }

*/