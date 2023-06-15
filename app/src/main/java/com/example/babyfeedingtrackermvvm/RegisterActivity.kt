package com.example.babyfeedingtrackermvvm

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.babyfeedingtrackermvvm.databinding.ActivityRegisterBinding

class RegisterActivity: AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterBinding

    var clickedSquareId: Int = 0
    var hexColor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ViewModel
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        //Layout
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Events
        binding.buttonLogin.setOnClickListener {
            viewModel.doLogin(binding.editUsername.text.toString(), binding.editStation.text.toString(), hexColor)
        }

        //Observers
        observe()


    }

    //functions

    private fun observe() {
        viewModel.loginMessage.observe(this) {

            if(it != "success") {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            } else {
                // TODO: prosseguir com o login
                //startActivity(Intent(applicationContext, MainActivity::class.java))
                //finish()
            }
        }
    }

    fun onSquareClicked(view: View) {

        clickedSquareId = view.id
        Log.d("clicking squares", "clickedSquareId $clickedSquareId")

        if (clickedSquareId == R.id.purple_square) {
            view.background.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                PorterDuff.Mode.SRC_ATOP
            )

            binding.blueSquare.background.clearColorFilter()
            hexColor = "#" + Integer.toHexString(ContextCompat.getColor(this, android.R.color.holo_purple))
            // Square 1 was clicked

        } else if (clickedSquareId == R.id.blue_square) {
            view.background.setColorFilter(
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                PorterDuff.Mode.SRC_ATOP
            )

            binding.purpleSquare.background.clearColorFilter()
            hexColor = "#" + Integer.toHexString(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
            // Square 2 was clicked
        }
    }
}

// TODO: antes de continuar -> preciso implementar o Login na API (mesmas infos do registro, sem password por enquanto), atualmente o user só registra
    //criar RegisterActivity (e as coisas relacionadas)
// TODO: depois de terminar de refatorar o código aqui, vou implementar JWT

//commit info: updated gradle with ViewModel and Retrofit necessary dependencies