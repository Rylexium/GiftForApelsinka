package com.example.gift_for_apelsinka.service.receiver

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.NOTIFICATION_CHANNEL_ID_RANDOM_QUESTION
import com.example.gift_for_apelsinka.cache.channelNameRandomQuestion
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.util.*

class RandomQuestionReceiver : BroadcastReceiver() {
    private val KEY_TIMETABLE = "EquationRandomTimetable"
    private val KEY_TEXT = "EquationRandomText"
    private val DELAY_FOR_NEXT_NOTIFICATION = 30 //minute

    private lateinit var ctx : Context
    private lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sharedPreferences = context.getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        ctx = context

        val timetable = Gson().fromJson(sharedPreferences?.getString(KEY_TIMETABLE, Gson().toJson(Calendar.getInstance())), Calendar::class.java)

        val nowCalendar = Calendar.getInstance()

        Log.e("now", Calendar.getInstance().time.toString() + " " + Calendar.getInstance().timeInMillis)
        Log.e("timetable", timetable.time.toString() + " " + timetable.timeInMillis.toString())

        if(nowCalendar >= timetable) {

            var text = sharedPreferences.getString(KEY_TEXT, Notifaction.generateTextOfEquation())

            CoroutineScope(Dispatchers.IO).launch {
                equationNotification(text.toString())

                //timetable.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR) + 1)
                timetable.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY))
                timetable.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE) + DELAY_FOR_NEXT_NOTIFICATION)
                timetable.set(Calendar.SECOND, 0)

                val previousText = "Текущие случайный вопрос : $text"

                text = Notifaction.generateTextOfEquation()

                NetworkMessage.sendMessage(2, 2, "$previousText\nСледующий случайный вопрос : " +
                        "${timetable.get(Calendar.HOUR_OF_DAY)} : ${timetable.get(Calendar.MINUTE)}, $text")

                val timetableJson = Gson().toJson(timetable)
                sharedPreferences.edit()
                    .putString(KEY_TIMETABLE, timetableJson)
                    .putString(KEY_TEXT, text)
                    .apply()

                val alarmManager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager

                val pendingIntent = PendingIntent.getBroadcast(context, 3, Intent(context, RandomQuestionReceiver::class.java), 0)
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timetable.timeInMillis, pendingIntent)
            }
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
            .setContentTitle("Вопросик")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }
}