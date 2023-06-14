package com.example.babyfeedingtrackermvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.babyfeedingtrackermvvm.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //ViewModel
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        //Layout
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Events


        //Observers


    }

    //functions
}