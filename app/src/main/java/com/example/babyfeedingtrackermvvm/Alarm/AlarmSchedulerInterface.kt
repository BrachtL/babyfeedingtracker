package com.example.babyfeedingtrackermvvm.Alarm

interface AlarmSchedulerInterface {
    fun scheduleAlarm(alarmTimeMillis: Long)
    fun cancelAlarm()
}

