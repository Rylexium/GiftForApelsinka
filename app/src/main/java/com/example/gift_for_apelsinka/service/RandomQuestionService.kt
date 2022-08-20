package com.example.gift_for_apelsinka.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction.generateTextOfEquation
import com.example.gift_for_apelsinka.util.WorkWithTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RandomQuestionService : Service() {

    private val KEY_HOUR = "EquationRandomHour"
    private val KEY_MINUTE = "EquationRandomMinute"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        var randomHour = sharedPreferences.getInt(KEY_HOUR, 20)
        var randomMinute = sharedPreferences.getInt(KEY_MINUTE, 30)
        Thread {
            while (true) {
                val nowHour = WorkWithTime.getNowHour()
                val nowMinute = WorkWithTime.getNowMinute()
                if(nowHour == randomHour && randomMinute <= nowMinute) {
                    equationNotification()
                    randomHour = (16..23).random()
                    randomMinute = (System.currentTimeMillis() % 59).toInt()
                    sharedPreferences.edit()
                        .putInt(KEY_HOUR, randomHour)
                        .putInt(KEY_MINUTE, randomMinute)
                        .apply()
                }
                Thread.sleep(5_000)
            }
        }.start()
        return START_REDELIVER_INTENT
    }

    private fun equationNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        CoroutineScope(Dispatchers.IO).launch {
            val id = when((1..3).random()) {
                1 -> R.drawable.developer
                2 -> R.drawable.icon_of_developer
                else -> { R.drawable.lexa1 }
            }
            val circleImage = InitView.getCircleImage(id, applicationContext)
            val notificationGoodMorning = NotificationCompat.Builder(applicationContext, "CHANNEL_QUESTION")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setLargeIcon(circleImage)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Вопросик")
                .setContentText(generateTextOfEquation())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val notifChannel = NotificationChannel("CHANNEL_QUESTION", "CHANNEL_QUESTION", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(notifChannel)
            }
            notificationManager.notify(2, notificationGoodMorning)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}