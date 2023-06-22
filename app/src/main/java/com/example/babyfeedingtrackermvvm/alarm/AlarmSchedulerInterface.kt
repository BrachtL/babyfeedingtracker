package com.example.babyfeedingtrackermvvm.alarm

import android.content.Context

interface AlarmSchedulerInterface {
    fun scheduleAlarm(alarmTimeMillis: Long, context: Context)
    fun cancelAlarm(context: Context)
}

