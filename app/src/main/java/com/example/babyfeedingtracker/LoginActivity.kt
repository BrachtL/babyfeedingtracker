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


class LoginActivity : AppCompatActivity() {

    var clickedSquareId: Int = 0
    var hexColor: String = "0"

    fun onSquareClicked(view: View) {

        clickedSquareId = view.id
        Log.d("clicking squares", "clickedSquareId ${clickedSquareId}")

        if (clickedSquareId == R.id.purple_square) {

            view.background.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                PorterDuff.Mode.SRC_ATOP
            )

            findViewById<View>(R.id.blue_square).background.clearColorFilter()


            hexColor = Integer.toHexString(ContextCompat.getColor(this, android.R.color.holo_purple))
            // Square 1 was clicked
        } else if (clickedSquareId == R.id.blue_square) {

            view.background.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                PorterDuff.Mode.SRC_ATOP
            )

            findViewById<View>(R.id.purple_square).background.clearColorFilter()


            hexColor = Integer.toHexString(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
            // Square 2 was clicked
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton: Button = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            // Get the data from the EditText fields
            val username: String = findViewById<EditText>(R.id.username_edit_text).text.toString()
            val station: String = findViewById<EditText>(R.id.baby_name_edit_text).text.toString()

            Log.d("hex color", hexColor)


            // TODO: salvar em sharedPreferences e trocar de atividade
                //não preciso enviar ao db agora, pois vou enviar em cada post
            /*
            // Save the data to shared preferences
            val sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString(getString(R.string.saved_name_key), name)
                putString(getString(R.string.saved_email_key), email)
                apply()
            }

            // Go back to the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish the SetupActivity so that the user cannot go back to it
            */
        }
    }
}