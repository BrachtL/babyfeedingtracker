package com.example.babyfeedingtrackermvvm.view

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.babyfeedingtrackermvvm.R
import com.example.babyfeedingtrackermvvm.viewmodel.RegisterViewModel
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
            viewModel.doRegister(binding.editUsername.text.toString(), binding.editStation.text.toString(), hexColor)
        }

        //Observers
        observe()
    }

    //functions

    private fun observe() {
        viewModel.registerMessage.observe(this) {

            //cases:
                //"userAlreadyInTheStation" -> dont change activity: choose another name (or log in)
                //"userIsNowPending" -> change to MainActivity (in the future -> change to a PendingActivity and user has to wait the approval, or cancel the request)
                    //3 things take user out this PendingActivity: cancel button, be approved by the owner or be rejected by the owner
                //"userIsNowOwner" -> change activity

            if(it == "userIsNowOwner" || it == "userIsNowPending") {
                //Toast.makeText(this, "CHANGE ACTIVITY: " + it, Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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

// TODO: preciso implementar o Login na API (mesmas infos do registro, sem password por enquanto), atualmente o user só registra
// TODO: JWT