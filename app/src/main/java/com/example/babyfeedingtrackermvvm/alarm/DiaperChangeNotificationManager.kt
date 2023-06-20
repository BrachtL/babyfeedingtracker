package com.example.babyfeedingtrackermvvm.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.babyfeedingtrackermvvm.R

// TODO: trocar nome desta classe
// TODO: testar com API 25
// TODO: testar múltiplas calls da notifyDiaperChange para ver se atualiza sempre a mesma
// TODO: ver como o Ferrari fez a classe fechada dele (Client eu acho)
class DiaperChangeNotificationManager {
    private lateinit var notificationManager: NotificationManager
    private lateinit var diaperChangeBuilder: NotificationCompat.Builder

    private val channelIdChange = "diaper_change_reminder"
    private val channelNameChange = "Diaper Change Reminder"
    private val channelDescriptionChange = "Receive reminders when it's time to change baby's diaper"

    //if same value is used, the notification will be "replaced", "updated"
    val checkId = 1 // a unique id for the notification each time it appears
    val changeId = 2

    //notificationManager.cancel(checkId)
    //notificationManager.notify(changeId, diaperChangeBuilder.build())

    //notificationManager.notify(checkId, diaperCheckBuilder.build())

    fun notifyDiaperChange(context: Context) {
        if (!::diaperChangeBuilder.isInitialized) {
            diaperChangeBuilder = getDiaperChangeBuilder(context)
        }

        if (!::notificationManager.isInitialized) {
            notificationManager = getNotificationManagerInstance(context)
        }

        notificationManager.notify(changeId, diaperChangeBuilder.build())
    }

    private fun getDiaperChangeBuilder(context: Context): NotificationCompat.Builder {
        if(!::diaperChangeBuilder.isInitialized) {
            val defaultNotificationSoundUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            diaperChangeBuilder = NotificationCompat.Builder(context, channelIdChange)
                .setSmallIcon(R.drawable.diaper1)
                .setColor(ContextCompat.getColor(context, R.color.diaper_background_pink))
                .setContentTitle("É hora de trocar as fraldas \\o/")
                .setContentText("Seu bebê precisa de fraldas limpinhas")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultNotificationSoundUri)

            return diaperChangeBuilder
        } else {
            return diaperChangeBuilder
        }
    }

    private fun getNotificationManagerInstance(context: Context): NotificationManager {

        if(!::notificationManager.isInitialized) {

            val importance = NotificationManager.IMPORTANCE_HIGH

            if (Build.VERSION.SDK_INT >= 26) {

                val notificationChannel = NotificationChannel(channelIdChange, channelNameChange, importance)
                notificationChannel.description = channelDescriptionChange

                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                notificationManager.createNotificationChannel(notificationChannel)

            } else {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            /*
            val diaperCheckBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.diaper1)
                .setColor(ContextCompat.getColor(context, R.color.diaper_background_pink))
                .setContentTitle("Checando se precisa trocar as fraldas")
                .setContentText("Aguarde")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultNotificationSoundUri)
            */

            return notificationManager
        } else {
            return notificationManager
        }

    }


}