package com.example.babyfeedingtrackermvvm.Alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle the alarm event here
        Toast.makeText(context, "Alarm was triggered", Toast.LENGTH_SHORT).show()

    }
}