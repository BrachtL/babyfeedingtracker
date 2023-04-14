package com.example.babyfeedingtracker

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import com.jaredrummler.materialspinner.MaterialSpinner
import com.jaredrummler.materialspinner.MaterialSpinnerBaseAdapter
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Request
import java.io.IOException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

// TODO: Return to this activity if station and user are not found

class LoginActivity : AppCompatActivity() {

    var clickedSquareId: Int = 0
    var hexColor: String = "0"

    //check which square is clicked, if none is clicked, clickedSquareId = 0
    fun onSquareClicked(view: View) {

        clickedSquareId = view.id
        Log.d("clicking squares", "clickedSquareId ${clickedSquareId}")

        if (clickedSquareId == R.id.purple_square) {
            view.background.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                PorterDuff.Mode.SRC_ATOP
            )

            findViewById<View>(R.id.blue_square).background.clearColorFilter()
            hexColor = "#" + Integer.toHexString(ContextCompat.getColor(this, android.R.color.holo_purple))
            // Square 1 was clicked
        } else if (clickedSquareId == R.id.blue_square) {

            view.background.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                PorterDuff.Mode.SRC_ATOP
            )

            findViewById<View>(R.id.purple_square).background.clearColorFilter()
            hexColor = "#" + Integer.toHexString(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
            // Square 2 was clicked
        }
    }

    fun validateFieldString(field: String, fieldName: String): String {

        if(fieldName == "baby name") {
            if(field.length < 6) {
                val toast = Toast.makeText(this, "Please enter at least 6 chars for your $fieldName", Toast.LENGTH_SHORT)
                toast.show()
                return "length"
            }
        } else if(fieldName == "username") {
            if(field.length > 7 || field.length < 3) {
                val toast = Toast.makeText(this, "username has to have between 3 and 7 chars", Toast.LENGTH_SHORT)
                toast.show()
                return "length"
            }
        } else {
            return "not username nor baby name"
        }

        val prohibitedChars = " '`|#$*/-,;()\\" + "\n" + '"'

        for(fieldChar in field) {
            for (prohibitedChar in prohibitedChars) {
                if (fieldChar == prohibitedChar) {
                    val toast = Toast.makeText(this,"Please do not use special chars", Toast.LENGTH_SHORT)
                    toast.show()
                    return "specialChar"
                }
            }
        }
        return "valid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.isEnabled = true
        loginButton.text = "LOGIN"


        loginButton.setOnClickListener {
            loginButton.text = "LOADING"
            loginButton.isEnabled = false
            // Get the data from the EditText fields
            val username: String = findViewById<EditText>(R.id.username_edit_text).text.toString()
            val station: String = findViewById<EditText>(R.id.baby_name_edit_text).text.toString()
            val userColor: String = hexColor

            var isUsernameValid = false
            var isStationValid = false
            var isColorValid = false
            var isUsernameAvailable = false


            Log.d("hex color", hexColor)
            Log.d("username", username)

            if(validateFieldString(username, "username") == "valid") {
                isUsernameValid = true
                if(validateFieldString(station, "baby name") == "valid") {
                    isStationValid = true
                    if (userColor == "0") {
                        val toast =
                            Toast.makeText(this, "Please choose a color", Toast.LENGTH_SHORT)
                        toast.show()
                    } else {
                        isColorValid = true
                    }
                }
            }

            if(isColorValid && isStationValid && isUsernameValid) {
                val toast = Toast.makeText(this, "Everything is valid!", Toast.LENGTH_SHORT)
                //toast.show()

                fun checkAvailabilityAndPost(username: String, stationName: String, color: String) {
                    val JSON = "application/json; charset=utf-8".toMediaType()
                    val url = "https://lbracht-server-bft.glitch.me/station/checkAvailabilityAndPost"

                    val jsonObject = JSONObject().apply {
                        put("username", username)
                        put("station", stationName)
                        put("userColorClient", color)
                    }

                    val requestBody = jsonObject.toString().toRequestBody(JSON)

                    val client = OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS) // set connection timeout
                        .readTimeout(30, TimeUnit.SECONDS) // set read timeout
                        .writeTimeout(30, TimeUnit.SECONDS) // set write timeout
                        .build()

                    val request = Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build()

                    val toastError = Toast.makeText(this,"Error checkAvailabilityAndPost" +
                            "Username: $username" +
                            "stationName: $stationName" +
                            "color: $color", Toast.LENGTH_SHORT)
                    val toastPending = Toast.makeText(this,"User is now pending", Toast.LENGTH_SHORT)
                    val toastUserNotAvailable = Toast.makeText(this,"Not available username. Please choose another one", Toast.LENGTH_SHORT)
                    val toastOwner = Toast.makeText(this,"User is now the station's owner", Toast.LENGTH_SHORT)

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.d("Error checkAvailabilityAndPost", "onFailure" +
                                    "Username: $username" +
                                    "stationName: $stationName" +
                                    "color: $color")
                            toastError.show()
                        }

                        override fun onResponse(call: Call, response: Response) {

                            val responseBodyJSON = JSONObject(response.body!!.string())
                            Log.d("checkAvailabilityAndPost", "onResponse: $responseBodyJSON")
                            // TODO: handle pending and owner user cases here
                            when (responseBodyJSON.getString("message")) {
                                "userAlreadyInTheStation" -> {
                                    Log.d("checkAvailabilityAndPost", "Username is not available")
                                    toastUserNotAvailable.show()
                                }
                                "userIsNowPending" -> {
                                    Log.d("checkAvailabilityAndPost", "User is pending now")
                                    toastPending.show()
                                    isUsernameAvailable = true
                                }
                                "userIsNowOwner" -> {
                                    Log.d("checkAvailabilityAndPost", "User is the owner now")
                                    toastOwner.show()
                                    isUsernameAvailable = true
                                }
                            }

                            //I GOT SUPER POWERS for debugging purposes
                            if(username == "Luciano" && stationName == "LaraPBracht" ||
                                username == "Marceli" && stationName == "LaraPBracht") {
                                isUsernameAvailable = true
                            }

                            if (isUsernameAvailable) {
                                val loginLocalData =
                                    getSharedPreferences("login", Context.MODE_PRIVATE)
                                val loginLocalDataEditor = loginLocalData.edit()

                                loginLocalDataEditor.putString("username", username)
                                loginLocalDataEditor.putString("userColor", userColor)
                                loginLocalDataEditor.putString("station", station)

                                loginLocalDataEditor.apply()

                                runOnUiThread {
                                    loginButton.isEnabled = true
                                    loginButton.text = "LOGIN"
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }

                                //startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            } else {
                                runOnUiThread {
                                    loginButton.isEnabled = true
                                    loginButton.text = "LOGIN"
                                }
                            }
                            //}
                        }
                    })
                }

                // done: check here if the names are free to use or if someone else is using them
                checkAvailabilityAndPost(username, station, userColor)
                /*
                if(isUsernameAvailable) {
                    val loginLocalData = getSharedPreferences("login", Context.MODE_PRIVATE)
                    val loginLocalDataEditor = loginLocalData.edit()

                    loginLocalDataEditor.putString("username", username)
                    loginLocalDataEditor.putString("userColor", userColor)
                    loginLocalDataEditor.putString("station", station)

                    loginLocalDataEditor.apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    //finish() // Finish the SetupActivity so that the user cannot go back to it
                }
                */
            } else {
                loginButton.isEnabled = true
                loginButton.text = "LOGIN"
            }
        }
    }
}