package com.example.babyfeedingtracker
/*



//todo: criar a notificação de quando alguem trocou por voce
	ocorre quando acaba o timer e o valor durationMS sube ou quando entra no if que sobe o mesmo valor
	exceto quando a propria pessoa clica na fralda (set)
	checar se o nome de quem trocou é o mesmo, se não for, disparar a mensagem
		tem que receber da API o nome de quem trocou pra poder checkar

//todo: chamar a função get nos pontos certos

 */


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.abs

var TAG: String = "Testing diaper and timer"


class MainActivity : AppCompatActivity() {

    /*
    override fun onBackPressed() {
        //call finish in order not to return to LoginActivity
        //finish()
        finishAffinity()
    }
    */

    // TODO: apagar os campos que não tiverem dados no layout: username, amount e time
    // TODO: aparecer que não há dados, se a response vir vazia (user novo)

    // TODO: adicionar cores às médias (ex.: media 06 fica vermelha se abaixo da 12 ou 24)

    // TODO: adicionar onResume (chamar a função http de novo)
    // TODO: fazer botão para atualizar

    // TODO: fazer login (jwt, google, pegar senha e colocar hash...?)
    // TODO: fazer poder trocar de login

    // TODO: notificação de hora de amementar
    // TODO: notificação que alguem amamentou

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var changeDiaperTimer: CountDownTimer? = null

        val channelId = "diaper_change_reminder"
        val channelName = "Diaper Change Reminder"
        val channelDescription = "Receive reminders when it's time to change baby's diaper"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val defaultNotificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationChannel.description = channelDescription

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val checkId = 1 // a unique id for the notification each time it appears
        val changeId = 2

        //if same value is used, the notification will be "replaced", "updated"

        val diaperChangeBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.diaper1)
            .setColor(ContextCompat.getColor(this, R.color.diaper_background_pink))
            .setContentTitle("É hora de trocar as fraldas \\o/")
            .setContentText("Seu bebê precisa de fraldas limpinhas")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(defaultNotificationSoundUri)

        val diaperCheckBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.diaper1)
            .setColor(ContextCompat.getColor(this, R.color.diaper_background_pink))
            .setContentTitle("Checando se precisa trocar as fraldas")
            .setContentText("Aguarde")
            .setPriority(NotificationCompat.PRIORITY_HIGH)


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

            val diaperImageView: ImageView = findViewById(R.id.diaperImage)
            val mamadaImageView: ImageView = findViewById(R.id.mamadaImage)
            var timerText: TextView = findViewById<TextView>(R.id.timerText)
            mamadaImageView.isClickable = true
            diaperImageView.isClickable = true
            var durationMS = 2999999999L //14400000L //4hInMS


            var debug1 = 1
            fun getDiaperTimerDurationAndAnalyze(username: String, station: String) {
                //considerações:
                /*
                    //acho que toda fez que eu trocar o valor do duration, tenho que fazer um .cancel(), reinstanciar e um .start()
                    //excluir o código relacionado ao "first timer" e fazer sem, provavelmente não vai precisar
                    //o timer object vai ser manipulado tanto aqui quanto na função set
                    //se não tem dados no db, responde 4h em ms
                    //não preciso fazer valor default para o durationMS, pois o timer só começa quando eu instanciar, ou seja, quando tiver o primeiro duration
                    //meu cérebro fritou, mas quando eu estiver lendo ir de novo, vou conseguir fazer...
                    //tudo está funcionando separadamente, falta só juntar e tirar código de debug que injetei
                 */

                //diaperImageView.isClickable = false //it makes more sense let it active

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // set connection timeout
                    .readTimeout(30, TimeUnit.SECONDS) // set read timeout
                    .writeTimeout(30, TimeUnit.SECONDS) // set write timeout
                    .build()

                val urlBuilder = HttpUrl.Builder()
                    .scheme("https")
                    .host("lbracht-server-bft.glitch.me") //
                    .addPathSegment("getDiaperTimerDurationAndLastUsername")
                    .addQueryParameter("username", username)
                    .addQueryParameter("station", station)


                val url = urlBuilder.build()

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()


                val toastError = Toast.makeText(
                    this@MainActivity, "Error getDiaperTimerDurationAndAnalyze" +
                            "Username: $username" +
                            "station: $station"
                    , Toast.LENGTH_SHORT
                )

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(
                            "Error getDiaperTimerDurationAndAnalyze", "onFailure" +
                                    "Username: $username" +
                                    "station: $station"
                        )
                        toastError.show()

                        runOnUiThread {
                            diaperImageView.isClickable = true
                            // TODO: devo cancelar as duas notificações aqui?
                        }
                    }

                    //onResponse, chamar timer.cancel(), instanciar de novo com o restante do tempo e chamar
                    //se o tempo for menor que 10 minutos ou se já passou, enviar notificação de troca de fralda e trocar a imagem para o gif
                    // TODO: vou mandar um request get quando clicar na mamadeira, quando terminar o timer, onCreate e onResume
                    // TODO: atualiza o valor somente se a diferença for mais de 10m

                    override fun onResponse(call: Call, response: Response) {
                        val responseBodyJSON = JSONObject(response.body!!.string())

                        var timerDurationDb = responseBodyJSON.getString("timerDuration").toLong()
                        val messageDb = responseBodyJSON.getString("message")
                        val lastUsername = responseBodyJSON.getString("lastUsername")
                        Log.d(TAG, "onResponse: lastUsername = $lastUsername\nmessageDb = $messageDb\ntimerDurationDb = $timerDurationDb")


                        Log.d("getDiaperTimerDurationAndAnalyze", "onResponse: $responseBodyJSON")

                        runOnUiThread {


                            //acho que esse if nao faz mais sentido, visto que vou ter que instanciar todas as vezes
                            //acho que toda fez que eu trocar o valor do duration, tenho que fazer um .cancel(), reinstanciar e um .start()
                            //if (changeDiaperTimer == null) {
                                // create new timer and assign it to changeDiaperTimer
                            /*
                                changeDiaperTimer = object : CountDownTimer(durationMS, 1000) { //14400000 //4h
                                    override fun onTick(millisUntilFinished: Long) {
                                        durationMS = millisUntilFinished
                                        var hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                                        var minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hours))
                                        var seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
                                        timerText.text = "$hours:$minutes:$seconds"
                                        //timerText.text = "$durationMS:$1" // TODO: remove this line
                                        Log.d(TAG, "onTick: durationMS: $durationMS")

                                    }

                                    override fun onFinish() {
                                        notificationManager.notify(checkId, diaperCheckBuilder.build())
                                        getDiaperTimerDurationAndAnalyze(username!!, station!!)
                                        Log.d(TAG, "onFinish: finish happens")
                                    }
                                }
                            //}
                            */



                            /*
                            //start the first timer
                            if (durationMS == 2999999999L) {
                                durationMS = 7000L //4hInMS todo: change it to 14400000L

                                //changeDiaperTimer.cancel()
                                //changeDiaperTimer.start()
                                Log.d(TAG, "Estive no if first timer")
                            }

                            */

                            //changeDiaperTimer.cancel()



                            if(timerDurationDb <= 0) { //old: if(timerDurationDb <= 60000) {
                                val hexColor = "#" + Integer.toHexString(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))
                                val colorInt = Color.parseColor(hexColor)

                                timerText.setTextColor(colorInt)

                                Glide.with(this@MainActivity)
                                    .load(R.drawable.poopgif1)
                                    .into(diaperImageView)

                                notificationManager.cancel(checkId) //todo: check notification vai ser sempre criada no onFinish e encerrada nessa função
                                notificationManager.notify(changeId, diaperChangeBuilder.build())

                                if (changeDiaperTimer != null) {
                                    changeDiaperTimer!!.cancel()
                                }
                                durationMS = -1L
                                /*//todo: put a chronometer here
                                durationMS = timerDurationDb
                                changeDiaperTimer = object : CountDownTimer(durationMS, 1000) { //14400000 //4h
                                    override fun onTick(millisUntilFinished: Long) {
                                        durationMS = millisUntilFinished
                                        var hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                                        var minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hours))
                                        var seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
                                        timerText.text = "$hours:$minutes:$seconds"
                                        //timerText.text = "$durationMS:$1" // TODO: remove this line
                                        Log.d(TAG, "onTick: durationMS: $durationMS")

                                    }

                                    override fun onFinish() {
                                        notificationManager.notify(checkId, diaperCheckBuilder.build())
                                        getDiaperTimerDurationAndAnalyze(username!!, station!!)
                                        Log.d(TAG, "onFinish: finish happens")
                                    }
                                }
                                changeDiaperTimer!!.start()
                                */
                            } else {

                                if ((abs(timerDurationDb - durationMS) > 60000) || durationMS == -1L) {
                                    if (changeDiaperTimer != null) {
                                        changeDiaperTimer!!.cancel()
                                    }
                                    Log.d(TAG, "onResponse: I AM HERE IN THE TIMER!!!")
                                    durationMS = timerDurationDb
                                    changeDiaperTimer = object : CountDownTimer(durationMS, 1000) { //14400000 //4h
                                        override fun onTick(millisUntilFinished: Long) {
                                            durationMS = millisUntilFinished
                                            var hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                                            var minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hours))
                                            var seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))

                                            timerText.text = String.format("%01d:%02d:%02d", hours, minutes, seconds)
                                            //timerText.text = "$durationMS:$1" // TODO: remove this line
                                            Log.d(TAG, "onTick: durationMS: $durationMS")

                                        }

                                        override fun onFinish() {
                                            notificationManager.notify(checkId, diaperCheckBuilder.build())
                                            getDiaperTimerDurationAndAnalyze(username!!, station!!)
                                            Log.d(TAG, "onFinish: finish happens")
                                        }
                                    }
                                    changeDiaperTimer!!.start()
                                    Log.d(TAG, "onResponse: atualizei a duration com o db: $durationMS")
                                }

                                val hexColor = "#" + Integer.toHexString(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        android.R.color.holo_blue_dark
                                    )
                                )
                                val colorInt = Color.parseColor(hexColor)

                                timerText.setTextColor(colorInt)

                                diaperImageView.setImageResource(R.drawable.diaper2)

                                notificationManager.cancel(changeId)
                                notificationManager.cancel(checkId) //todo: check notification vai ser sempre criada no onFinish e encerrada nessa função
                            }
                            diaperImageView.isClickable = true

                            /*
                            if(debug1%2 == 1) {
                                changeDiaperTimer = object : CountDownTimer(durationMS, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        durationMS = millisUntilFinished
                                        var hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                                        var minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hours))
                                        var seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))
                                        timerText.text = "$hours:$minutes:$seconds"
                                        //timerText.text = "$durationMS:$1" // TODO: remove this line
                                        Log.d(TAG, "onTick: durationMS: $durationMS")
                                    }

                                    override fun onFinish() {
                                        //notificationManager.notify(notificationId, diaperCheckBuilder.build())
                                        //esse gif vai sair daqui e a notificação tbm
                                        getDiaperTimerDurationAndAnalyze(username!!, station!!)
                                        Log.d(TAG, "onFinish: finish happens")
                                    }
                                }
                                    

                                changeDiaperTimer!!.start()
                                Log.d(TAG, "onResponse: LIGUEI O TIMER!!!")
                            } else {
                                changeDiaperTimer!!.cancel()
                                Log.d(TAG, "onResponse: DESLIGUEI O TIMER!!!")
                            }

                            debug1++

                            */


                        }
                    }
                })
            }

            fun setDiaperChangeTime(username: String, station: String) {

                //diaperImageView.isClickable = false //it makes more sense let it active

                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // set connection timeout
                    .readTimeout(30, TimeUnit.SECONDS) // set read timeout
                    .writeTimeout(30, TimeUnit.SECONDS) // set write timeout
                    .build()

                val urlBuilder = HttpUrl.Builder()
                    .scheme("https")
                    .host("lbracht-server-bft.glitch.me") //
                    .addPathSegment("setDiaperChangeTime")
                    .addQueryParameter("username", username)
                    .addQueryParameter("station", station)


                val url = urlBuilder.build()

                val request = Request.Builder()
                    .url(url)
                    .post(RequestBody.create(null, ""))
                    .build()


                val toastError = Toast.makeText(
                    this@MainActivity, "Error setDiaperChangeTime" +
                            "Username: $username" +
                            "station: $station"
                    , Toast.LENGTH_SHORT
                )

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(
                            "Error setDiaperChangeTime", "onFailure" +
                                    "Username: $username" +
                                    "station: $station"
                        )
                        toastError.show()

                        runOnUiThread {
                            diaperImageView.isClickable = true
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val responseBodyJSON = JSONObject(response.body!!.string())

                        val timerDurationDb = responseBodyJSON.getString("timerDuration").toLong()
                        val messageDb = responseBodyJSON.getString("message")


                        Log.d("setDiaperChangeTime", "onResponse: $responseBodyJSON")

                        runOnUiThread {

                            if (changeDiaperTimer != null) {
                                changeDiaperTimer!!.cancel()
                            }
                            durationMS = timerDurationDb
                            changeDiaperTimer = object : CountDownTimer(durationMS, 1000) { //14400000 //4h
                                override fun onTick(millisUntilFinished: Long) {
                                    durationMS = millisUntilFinished
                                    var hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                                    var minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished - TimeUnit.HOURS.toMillis(hours))
                                    var seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes))

                                    timerText.text = String.format("%01d:%02d:%02d", hours, minutes, seconds)
                                    //timerText.text = "$durationMS:$1" // TODO: remove this line
                                    Log.d(TAG, "onTick: durationMS: $durationMS")

                                }

                                override fun onFinish() {
                                    notificationManager.notify(checkId, diaperCheckBuilder.build())
                                    getDiaperTimerDurationAndAnalyze(username!!, station!!)
                                    Log.d(TAG, "onFinish: finish happens")
                                }
                            }
                            changeDiaperTimer!!.start()
                            Log.d(TAG, "onResponse: atualizei a duration com o db: $durationMS")

                            val hexColor = "#" + Integer.toHexString(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    android.R.color.holo_blue_dark
                                )
                            )
                            val colorInt = Color.parseColor(hexColor)

                            timerText.setTextColor(colorInt)

                            diaperImageView.setImageResource(R.drawable.diaper2)

                            notificationManager.cancel(changeId)
                            notificationManager.cancel(checkId) //todo: check notification vai ser sempre criada no onFinish e encerrada nessa função
                            diaperImageView.isClickable = true
                        }
                    }
                })
            }

            val timeEditText: EditText = findViewById<EditText>(R.id.editTextTime)

            timeEditText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Do nothing
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Do nothing
                }

                // TODO: add constraints here that don't allow invalid inputs.
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
                    this@MainActivity, "Error getMamadasScreenData" +
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
                    val toastBlank = Toast.makeText(this@MainActivity,
                        "Please enter the amount you fed",
                        Toast.LENGTH_SHORT)
                    toastBlank.show()
                    getMamadasScreenData(username!!, station!!, userColor!!)
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
                        this@MainActivity, "Error setMamada" +
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
                getDiaperTimerDurationAndAnalyze(username!!, station!!)
            }

            diaperImageView.setOnClickListener {

                //todo: popup: você trocou o bebê? //fazer isso depois, nao na primeira implementação
                    //se sim: chamar setDiaperTime(). Enviar station e username, timestamp pego do db
                    //como acabou de clicar em trocar, só dar .cancel() e .start() (nao precisa consultar o db daí, né)
                    //trocar imagem para a fralda

                setDiaperChangeTime(username!!, station!!)
            }

            getDiaperTimerDurationAndAnalyze(username!!, station!!)
            getMamadasScreenData(username!!, station!!, userColor!!)
        }
    }
}

