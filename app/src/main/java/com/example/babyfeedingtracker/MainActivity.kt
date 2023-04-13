package com.example.babyfeedingtracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginLocalData = getSharedPreferences("login", Context.MODE_PRIVATE)

        val username = loginLocalData.getString("username", "noData")
        val userColor = loginLocalData.getString("userColor", "noData") //Maybe I should put it also in the activity_main.xml, maybe remove it from here
        val station = loginLocalData.getString("station", "noData")

        if(username == "noData" || station == "noData") {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}