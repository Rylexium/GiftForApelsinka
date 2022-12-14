package com.example.gift_for_apelsinka.service.receiver.repeat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.NOTIFICATION_CHANNEL_ID_RANDOM_QUESTION
import com.example.gift_for_apelsinka.cache.channelNameRandomQuestion
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithServices.alarmTask
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.util.*

class RandomQuestionReceiver : BroadcastReceiver() {
    companion object {
        val KEY_TIMETABLE = "EquationRandomTimetable"
    }
    private val KEY_TEXT = "EquationRandomText"

    private lateinit var ctx : Context
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sharedPreferences = context.getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        ctx = context

        val timetable = Gson().fromJson(sharedPreferences?.getString(KEY_TIMETABLE, Gson().toJson(Calendar.getInstance())), Calendar::class.java)

        val nowCalendar = Calendar.getInstance()

        var text = sharedPreferences.getString(KEY_TEXT, Notifaction.generateTextOfEquation())


        CoroutineScope(Dispatchers.IO).launch {
            equationNotification(text.toString())

            timetable.timeInMillis = nowCalendar.timeInMillis

            val max = 23
            val min = 16
            val hour = Random().nextInt(max + 1 - min) + min

            var isNextDay = false
            if(nowCalendar.get(Calendar.HOUR_OF_DAY) > hour) {
                isNextDay = true
                timetable.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR) + 1)
            }

            timetable.set(Calendar.HOUR_OF_DAY, hour)
            timetable.set(Calendar.MINUTE, Random().nextInt(59))
            timetable.set(Calendar.SECOND, 0)
            timetable.set(Calendar.MILLISECOND, 0)

            val previousText = "?????????????? ?????????????????? ???????????? : $text"

            text = Notifaction.generateTextOfEquation()

            NetworkMessage.sendMessage(2, 2, "$previousText\n?????????????????? ?????????????????? ???????????? ${if(isNextDay) "(????????????)" else "(??????????????)"} : " +
                    "${if(timetable.get(Calendar.HOUR_OF_DAY) < 10) "0" + timetable.get(Calendar.HOUR_OF_DAY) else timetable.get(Calendar.HOUR_OF_DAY)} : " +
                    "${if(timetable.get(Calendar.MINUTE) < 10) "0" + timetable.get(Calendar.MINUTE) else timetable.get(Calendar.MINUTE)}, $text")

            sharedPreferences.edit()
                .putString(KEY_TIMETABLE, Gson().toJson(timetable))
                .putString(KEY_TEXT, text)
                .apply()
            alarmTask(context, timetable, RandomQuestionReceiver::class.java)
        }

    }

    private fun equationNotification(text : String) {
        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID_RANDOM_QUESTION, channelNameRandomQuestion, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notifChannel)
        }
        notificationManager.notify(6, getNotification(text))
    }

    private fun getNotification(text : String) = runBlocking {
        val id = when(Random().nextInt(3)) {
            0 -> R.drawable.developer
            1 -> R.drawable.icon_of_developer
            else -> { R.drawable.lexa1 }
        }
        val circleImage = async { InitView.getCircleImage(id, ctx) }
        return@runBlocking NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL_ID_RANDOM_QUESTION)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setLargeIcon(circleImage.await())
            .setWhen(System.currentTimeMillis())
            .setContentTitle("????????????????")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }
}