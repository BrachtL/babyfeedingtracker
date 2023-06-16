package com.example.babyfeedingtrackermvvm.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.babyfeedingtrackermvvm.R


// TODO: carregar o que precisa aqui do outro código primeiro (layout)
// TODO: verificar se está logado, se não estiver, mandar para a RegisterActivity

// TODO: para depois:
    //fazer a register activity ser uma só para registro e login, com um botão de registro e outro de login
    //se um usuário pendente tentar logar, falar que está pendente e perguntar se quer cancelar
    //criar logout button

    //do the logic for deactivate login button after click it (new file?)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}