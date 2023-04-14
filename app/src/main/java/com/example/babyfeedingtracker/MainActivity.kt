package com.example.babyfeedingtracker

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

var TAG: String = "Testing click on an imageview"


class MainActivity : AppCompatActivity() {

    /*
    override fun onBackPressed() {
        //call finish in order not to return to LoginActivity
        //finish()
        finishAffinity()
    }
    */

    // TODO: apagar os campos que não tiverem dados
    // TODO: aparecer que não há dados, se a response vir vazia (user novo)

    // TODO: adicionar cores às médias (ex.: media 06 fica vermelha se abaixo da 12 ou 24)

    // TODO: adicionar onResume (chamar a função http de novo)
    // TODO: fazer botão para atualizar

    // TODO: fazer login (jwt, google, pegar senha e colocar hash...?)
    // TODO: fazer poder trocar de login

    // TODO: notificação de hora de amementar
    // TODO: notificação que alguem amamentou

    // TODO: fazer o merge da mamada postada com menos de 30min da última mamada: soma os amounts e fas a média do tempo
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginLocalData = getSharedPreferences("login", Context.MODE_PRIVATE)

        val username = loginLocalData.getString("username", "noData")
        val userColor = loginLocalData.getString(
            "userColor",
            "noData"
        ) //Maybe I should put it also in the activity_main.xml, maybe remove it from here
        val station = loginLocalData.getString("station", "noData")

        if (username == "noData" || station == "noData") {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } else {

            Log.d(
                TAG, "onCreate, sharedPreferences data: username:" +
                        "$username, userColor: $userColor and station: $station"
            )

            val mamadaImageView: ImageView = findViewById(R.id.mamadaImage)
            mamadaImageView.isClickable = true

            val timeEditText: EditText = findViewById<EditText>(R.id.editTextTime)

            timeEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Do nothing
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Do nothing
                }

                // TODO: add constraints here that doesn't allow invalid inputs.
                //  It is a if puzzle! :D
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 2 && before == 0) {
                        timeEditText.setText("${s}:")
                        timeEditText.setSelection(timeEditText.text.length)
                        Log.d(TAG, "onTextChanged: I WAS HERE")
                    }
                    if(s?.length == 3 && before == 0 && !s.contains(':')) {
                        timeEditText.setText("${s[0]}${s[1]}:${s[1]}")
                        timeEditText.setSelection(timeEditText.text.length)
                    }
                }
            })

            val amountEditText: EditText = findViewById<EditText>(R.id.editTextAmount)

            fun getMamadasScreenData(username: String, stationName: String, color: String) {
                // TODO: mostrar LOADING e desabilitar botão

                mamadaImageView.setImageResource(R.drawable.bottle2_bw93)
                mamadaImageView.isClickable = false

                val JSON = "application/json; charset=utf-8".toMediaType()
                val url = "https://lbracht-server-bft.glitch.me/getMamadasScreenData"

                val jsonObject = JSONObject().apply {
                    put("username", username)
                    put("station", stationName)
                    //put("userColorClient", color)
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

                val toastError = Toast.makeText(
                    this, "Error getMamadasScreenData" +
                            "Username: $username" +
                            "stationName: $stationName"
                            //"color: $color"
                            , Toast.LENGTH_SHORT
                )
                //val toastPending = Toast.makeText(this,"User is now pending", Toast.LENGTH_SHORT)

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(
                            "Error getMamadasScreenData", "onFailure" +
                                    "Username: $username" +
                                    "stationName: $stationName"
                                    //"color: $color"
                        )
                        toastError.show()
                        mamadaImageView.setImageResource(R.drawable.bottle2)
                        mamadaImageView.isClickable = true
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBodyJSON = JSONObject(response.body!!.string())

                        val usernameArray = responseBodyJSON.getJSONArray("usernameArray")
                        //val stationArray = responseBodyJSON.getJSONArray("stationArray")
                        val timeArray = responseBodyJSON.getJSONArray("timeArray")
                        val amountArray = responseBodyJSON.getJSONArray("amountArray")
                        val colorArray = responseBodyJSON.getJSONArray("colorArray")

                        Log.d("getMamadasScreenData", "onResponse: $responseBodyJSON")


                        runOnUiThread {

                            val average06TextView: TextView = findViewById<TextView>(R.id.average06)
                            average06TextView.text = "06h: " + responseBodyJSON.getString("average06")
                            val average12TextView: TextView = findViewById<TextView>(R.id.average12)
                            average12TextView.text = "12h: " + responseBodyJSON.getString("average12")
                            val average24TextView: TextView = findViewById<TextView>(R.id.average24)
                            average24TextView.text = "24h: " + responseBodyJSON.getString("average24")

                            for (i in 0 until usernameArray.length()) {

                                val colorHex = colorArray.getString(i)
                                val colorInt = Color.parseColor(colorHex)

                                val usernameTextView: TextView = findViewById<TextView>(
                                    resources.getIdentifier(
                                        "username$i",
                                        "id",
                                        packageName
                                    )
                                )
                                usernameTextView.text = usernameArray.getString(i)
                                usernameTextView.setTextColor(colorInt)

                                val timeTextView: TextView = findViewById<TextView>(
                                    resources.getIdentifier(
                                        "time$i",
                                        "id",
                                        packageName
                                    )
                                )
                                timeTextView.text = timeArray.getString(i)
                                timeTextView.setTextColor(colorInt)

                                val amountTextView = findViewById<TextView>(resources.getIdentifier("amount$i", "id", packageName))
                                amountTextView.text = amountArray.getString(i) + "ml"
                                amountTextView.setTextColor(colorInt)
                            }

                            mamadaImageView.setImageResource(R.drawable.bottle2)
                            mamadaImageView.isClickable = true
                            Log.d(TAG, "onResponse: IMAGEM DEVERIA ESTAR COLORIDA")
                        }

                    }
                })
            }

            fun setMamada(username: String, stationName: String, time: String, amount: String) {

                if(amountEditText.text.toString() == "" || amountEditText.text.toString().toInt() == 0) {
                    val toastBlank = Toast.makeText(this,
                        "Please enter the amount you fed",
                        Toast.LENGTH_SHORT)
                    toastBlank.show()
                } else {

                    //val mamadaImageView: ImageView = findViewById(R.id.mamadaImage)
                    mamadaImageView.setImageResource(R.drawable.bottle2_bw93)
                    mamadaImageView.isClickable = false

                    val JSON = "application/json; charset=utf-8".toMediaType()
                    val url = "https://lbracht-server-bft.glitch.me/mamada"

                    val jsonObject = JSONObject().apply {
                        put("username", username)
                        put("station", stationName)
                        put("time", time)
                        put("amount", amount)
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

                    val toastError = Toast.makeText(
                        this, "Error setMamada" +
                                "Username: $username" +
                                "stationName: $stationName" +
                                "time: $time" +
                                "amount: $amount"
                        //"color: $color"
                        , Toast.LENGTH_SHORT
                    )
                    //val toastPending = Toast.makeText(this,"User is now pending", Toast.LENGTH_SHORT)

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.d(
                                "Error setMamadas", "onFailure" +
                                        "Username: $username" +
                                        "stationName: $stationName" +
                                        "time: $time" +
                                        "amount: $amount"
                                //"color: $color"
                            )
                            toastError.show()

                            mamadaImageView.setImageResource(R.drawable.bottle2)
                            mamadaImageView.isClickable = true
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val responseBodyJSON = JSONObject(response.body!!.string())

                            val message = responseBodyJSON.getString("message")

                            Log.d("setMamada", "onResponse: $responseBodyJSON")


                            runOnUiThread {

                                //val timeEditText: EditText = findViewById<EditText>(R.id.editTextTime)
                                timeEditText.setText("")
                                //val amountEditText: EditText = findViewById<EditText>(R.id.editTextAmount)
                                amountEditText.setText("")

                                mamadaImageView.setImageResource(R.drawable.bottle2)
                                mamadaImageView.isClickable = true

                                getMamadasScreenData(username!!, station!!, userColor!!)

                            }

                        }
                    })
                }
            }

            mamadaImageView.setOnClickListener {
                Log.d(TAG, "onCreate: mamadaImageView was clicked")
                setMamada(username!!, station!!, timeEditText.text.toString(), amountEditText.text.toString())
            }

            getMamadasScreenData(username!!, station!!, userColor!!)

        }
    }
}