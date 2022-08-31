package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowHour
import com.example.gift_for_apelsinka.util.WorkWithTime.getNowMinute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GoodMorningService : Service() {
    private val KEY_HOUR = "GoodMorningRandomHour"
    private val KEY_MINUTE = "GoodMorningRandomMinute"

    private val NOTIFICATION_CHANNEL_ID = "Канал доброго утра"
    private val channelName = "Канал доброго утра"

    private var backgroundThread : Thread? = null
    private var killThread = false

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("GoodMorningService", "onCreate")
        startMyOwnForeground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        startForeground(1,
            WorkWithServices.createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID, channelName, this@GoodMorningService))
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        killThread = false
        backgroundThread = taskGoodMorning()
        backgroundThread?.start()
    }

    override fun onDestroy() {
        killThread = true
        WorkWithServices.restartService(this, this.javaClass)
        WorkWithServices.startAllServices(this)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        WorkWithServices.restartService(this, this.javaClass)
        WorkWithServices.startAllServices(this)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    private fun taskGoodMorning() : Thread {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        var randomHour = sharedPreferences.getInt(KEY_HOUR, 8)
        var randomMinute = sharedPreferences.getInt(KEY_MINUTE, 45)

        return Thread {
            while (true) {
                if(killThread) break

                goodMorningNotification() //debug

                val nowHour = getNowHour()
                val nowMinute = getNowMinute()
                if((nowHour * 60 + nowMinute) >= (randomHour * 60 + randomMinute)) {
                    goodMorningNotification()

                    var max = 12
                    var min = 8
                    randomHour = java.util.Random().nextInt(((max - min) + 1) + min)

                    max = 59
                    min = 0
                    randomMinute = java.util.Random().nextInt(((max - min) + 1) + min)

                    CoroutineScope(Dispatchers.IO).launch {
                        NetworkMessage.sendMessage(2, 2, "Доброе утро : $randomHour : $randomMinute")
                    }

                    sharedPreferences.edit()
                        .putInt(KEY_HOUR, randomHour)
                        .putInt(KEY_MINUTE, randomMinute)
                        .apply()

                    Thread.sleep(61_200_000) // на 17 часов засыпаем
                }
                Thread.sleep(600_000) // 10 минут
            }
        }
    }

    private fun goodMorningNotification() {
        val notificationManager = this@GoodMorningService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        CoroutineScope(Dispatchers.IO).launch {
            val circleImageApelsinka = InitView.getCircleImage(R.drawable.mouse_of_apelsinka, this@GoodMorningService)
            val notificationGoodMorning = NotificationCompat.Builder(this@GoodMorningService, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setLargeIcon(circleImageApelsinka)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(Notifaction.generateTitleOfGoodMorning())
                .setContentText(Notifaction.generateTextOfGoodMorning())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(notifChannel)
            }
            notificationManager.notify(5, notificationGoodMorning)
        }
    }
}