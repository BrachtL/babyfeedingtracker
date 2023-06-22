package com.example.babyfeedingtrackermvvm.view

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.databinding.ActivityMainBinding
import com.example.babyfeedingtrackermvvm.viewmodel.MainViewModel
import org.w3c.dom.Text
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer
import kotlin.math.abs

/**
 * API is not being case sensitive on the station string
 */

// TODO depois:
    //fazer a register activity ser uma só para registro e login, com um botão de registro e outro de login
    //se um usuário pendente tentar logar, falar que está pendente e perguntar se quer cancelar
    //criar logout button

    //do the logic for deactivate login button after click it (new file?)

//@GlideModule
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ViewModel
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        disableBottle()

        viewModel.loadUserData()

        //Events
        binding.mamadaImage.setOnClickListener {
            if (binding.editTextAmount.text.toString() != "") {
                viewModel.setFeeding(binding.editTextAmount.text.toString().toInt())
            } else {
                viewModel.getFeedingData()
            }
        }
        binding.diaperImage.setOnClickListener {
            viewModel.setDiaperTimestamp()
        }

        //Observers
        observe()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDiaperData()
        Toast.makeText(this, "onResume()", Toast.LENGTH_SHORT).show()
        viewModel.getFeedingData()
        viewModel.removeDiaperNotification()
    }

    //functions

    private fun observe() {

        viewModel.isBottleDisabled.observe(this) {
            if (it) {
                disableBottle()
            } else {
                enableBottle()
            }
        }

        viewModel.screenObject.observe(this) {

            // TODO: make it a function
            binding.average06.text = "06h: ${it.average06}"
            binding.average12.text = "12h: ${it.average12}"
            binding.average24.text = "24h: ${it.average24}"

            for(i in 0 until it.usernameArray.size) {
                val colorHex = it.colorArray[i]
                val colorInt = Color.parseColor(colorHex)

                val usernameTextView = findViewById<TextView>(
                    resources.getIdentifier(
                        "username$i",
                        "id",
                        packageName
                    )
                )
                usernameTextView.text = it.usernameArray[i]
                usernameTextView.setTextColor(colorInt)

                val timeTextView = findViewById<TextView>(
                    resources.getIdentifier("time$i", "id", packageName)
                )
                timeTextView.text = it.timeArray[i]
                timeTextView.setTextColor(colorInt)

                val amountTextView = findViewById<TextView>(
                    resources.getIdentifier("amount$i", "id", packageName)
                )
                amountTextView.text = it.amountArray[i].toString() + "ml"
                amountTextView.setTextColor(colorInt)
            }
        }

        viewModel.isDirty.observe(this) {
            if (it) {
                Glide.with(this)
                    .load(R.drawable.poopgif1)
                    .into(binding.diaperImage)
                binding.timerText.setTextColor(getColor(R.color.red))
            } else {
                binding.diaperImage.setImageResource(R.drawable.diaper2)
                binding.timerText.setTextColor(getColor(android.R.color.holo_blue_dark))
            }
        }

        viewModel.timerText.observe(this) {
            val time = abs(it)

            val hours = TimeUnit.MILLISECONDS.toHours(time)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.HOURS.toMillis(hours))
            val seconds = TimeUnit.MILLISECONDS.toSeconds(time - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))

            binding.timerText.text = String.format("%01d:%02d:%02d", hours, minutes, seconds)
        }

        viewModel.failureMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoginValid.observe(this) {
            if(!it) {
                startActivity(Intent(applicationContext, RegisterActivity::class.java))
                finish()
            }
        }

        viewModel.eraseAmount.observe(this) {
            if(it) {
                binding.editTextAmount.setText("")
            }
        }
    }

    fun disableBottle() {
        binding.mamadaImage.setImageResource(R.drawable.bottle2_bw93)
        binding.mamadaImage.isClickable = false
    }

    fun enableBottle() {
        binding.mamadaImage.setImageResource(R.drawable.bottle2)
        binding.mamadaImage.isClickable = true
    }

}