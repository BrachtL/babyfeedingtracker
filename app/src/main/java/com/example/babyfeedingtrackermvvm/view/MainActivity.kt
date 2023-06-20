package com.example.babyfeedingtrackermvvm.view

import android.app.AlarmManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.babyfeedingtrackermvvm.alarm.AlarmScheduler
import com.example.babyfeedingtrackermvvm.databinding.ActivityMainBinding
import com.example.babyfeedingtrackermvvm.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * API is not being case sensitive on the station string
 */


// TODO depois : NEXT STEP: load diaper timer duration, implementando a loadData() e timerText na viewModel
    //criar DiaperRepository, DiaperModel?, DiaperService?, try not to create another file like APIGeneralResponse
// -> try to use likes this:
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

        viewModel.loadUserData()
        //viewModel.getDiaperData()

        //colocar em onResume?
        //viewModel.loadData() // TODO: essa função vai dar load em todos os dados, chamando uma função específica para cada tipo de dado

        //Observers
        observe()

        val alarmScheduler = AlarmScheduler(
            getSystemService(ALARM_SERVICE) as AlarmManager,
            applicationContext
        )

    }

    override fun onResume() {
        super.onResume()
        viewModel.getDiaperData()
        Toast.makeText(this, "onResume()", Toast.LENGTH_SHORT).show()
    }

    //functions

    private fun observe() {

        viewModel.timerTextColor.observe(this) {
            binding.timerText.setTextColor(getColor(it))
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

        /*
        viewModel.needToChange.observe(this) { // TODO: avisa quando tem que trocar a imagem

        }

        //viewModel.mamadaScreenData.observe(this) {} // TODO depois: dados para atualizar a tela, fazer em object (JSON?)
        */

        viewModel.isLoginValid.observe(this) {
            if(!it) {
                // TODO depois: change to RegisterActivity
            }
        }
    }

}