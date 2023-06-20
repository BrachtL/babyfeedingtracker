package com.example.babyfeedingtrackermvvm.alarm

interface AlarmSchedulerInterface {
    fun scheduleAlarm(alarmTimeMillis: Long)
    fun cancelAlarm()
}

