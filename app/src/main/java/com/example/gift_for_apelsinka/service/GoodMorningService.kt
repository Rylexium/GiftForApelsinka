package com.example.gift_for_apelsinka.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowMinute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class GoodMorningService : Service() {
    private val KEY_HOUR = "GoodMorningRandomHour"
    private val KEY_MINUTE = "GoodMorningRandomMinute"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        var randomHour = sharedPreferences.getInt(KEY_HOUR, 8)
        var randomMinute = sharedPreferences.getInt(KEY_MINUTE, 45)

        Thread {
            while (true) {
                val nowHour = getNowHour()
                val nowMinute = getNowMinute()
                if(nowHour == randomHour && randomMinute <= nowMinute) {
                    goodMorningNotification()
                    randomHour = (8..12).random()
                    randomMinute = (System.currentTimeMillis() % 59).toInt()
                    sharedPreferences.edit()
                        .putInt(KEY_HOUR, randomHour)
                        .putInt(KEY_MINUTE, randomMinute)
                        .apply()
                }
                Thread.sleep(5_000)
            }
        }.start()
        super.onStartCommand(intent, flags, startId)
        return START_REDELIVER_INTENT
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun goodMorningNotification() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        CoroutineScope(Dispatchers.IO).launch {
            val circleImageApelsinka = InitView.getCircleImage(R.drawable.mouse_of_apelsinka, applicationContext)
            val notificationGoodMorning = NotificationCompat.Builder(applicationContext, "CHANNEL_GOOD_MORNING")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setLargeIcon(circleImageApelsinka)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(Notifaction.generateTitleOfGoodMorning())
                .setContentText(Notifaction.generateTextOfGoodMorning())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val notifChannel = NotificationChannel("CHANNEL_GOOD_MORNING", "CHANNEL_GOOD_MORNING", NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(notifChannel)
            }
            notificationManager.notify(1, notificationGoodMorning)
        }
    }
}