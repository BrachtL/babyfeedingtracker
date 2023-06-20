package com.example.babyfeedingtrackermvvm.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.databinding.ActivityMainBinding
import com.example.babyfeedingtrackermvvm.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * API is not being case sensitive on the station string
 */

// TODO: ongoing: set diaper change

/*
val responseBodyJSON = JSONObject(response.body!!.string())

val usernameArray = responseBodyJSON.getJSONArray("usernameArray")
//val stationArray = responseBodyJSON.getJSONArray("stationArray")
val timeArray = responseBodyJSON.getJSONArray("timeArray")
val amountArray = responseBodyJSON.getJSONArray("amountArray")
val colorArray = responseBodyJSON.getJSONArray("colorArray")
*/

// TODO depois: verificar se está logado, se não estiver, mandar para a RegisterActivity

// TODO depois:
    //fazer a register activity ser uma só para registro e login, com um botão de registro e outro de login
    //se um usuário pendente tentar logar, falar que está pendente e perguntar se quer cancelar
    //criar logout button

    //do the logic for deactivate login button after click it (new file?)

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

        //Events
        binding.mamadaImage.setOnClickListener {
            //viewModel.setMamada(binding.editTextAmount)
        }
        binding.diaperImage.setOnClickListener {

        }

        viewModel.loadUserData() // TODO: essa função vai dar load em todos os dados, chamando uma função específica para cada tipo de dado

        //Observers
        observe()

        /*

        val alarmScheduler = AlarmScheduler(
            getSystemService(ALARM_SERVICE) as AlarmManager,
            applicationContext
        )

        */

    }

    override fun onResume() {
        super.onResume()
        viewModel.getDiaperData()
        Toast.makeText(this, "onResume()", Toast.LENGTH_SHORT).show()
    }

    //functions

    private fun observe() {

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
            //setar um alarm manager: quando chegar em zero, faz um novo request para a API
            val time = abs(it)

            val hours = TimeUnit.MILLISECONDS.toHours(time)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(time - TimeUnit.HOURS.toMillis(hours))
            val seconds = TimeUnit.MILLISECONDS.toSeconds(time - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))

            binding.timerText.text = String.format("%01d:%02d:%02d", hours, minutes, seconds)
        }

        viewModel.failureMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        //viewModel.mamadaScreenData.observe(this) {} // TODO depois: dados para atualizar a tela, fazer em object (JSON?)


        viewModel.isLoginValid.observe(this) {
            if(!it) {
                // TODO depois: change to RegisterActivity
            }
        }
    }

}