package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
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

    private var backgroundThread : Thread? = null

    @SuppressLint("NewApi")
    override fun onCreate() {
        startMyOwnForeground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "CHANNEL_GOOD_MORNING"
        val channelName = "CHANNEL_GOOD_MORNING"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.icon_of_developer)
            .setContentTitle("GM is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        notification.flags = notification.flags or Notification.VISIBILITY_SECRET
        startForeground(2, notification)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        backgroundThread = task()
        backgroundThread?.start()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val restartIntent = Intent(applicationContext, NotificationFromServerService::class.java)

        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getService(this, 1, restartIntent, PendingIntent.FLAG_ONE_SHOT);
        am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        task()
    }

    private fun task() : Thread {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        var randomHour = sharedPreferences.getInt(KEY_HOUR, 8)
        var randomMinute = sharedPreferences.getInt(KEY_MINUTE, 45)

        return Thread {
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
                Thread.sleep(600_000)
            }
        }
    }

    private fun goodMorningNotification() {
        val notificationManager = this@GoodMorningService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        CoroutineScope(Dispatchers.IO).launch {
            val circleImageApelsinka = InitView.getCircleImage(R.drawable.mouse_of_apelsinka, this@GoodMorningService)
            val notificationGoodMorning = NotificationCompat.Builder(this@GoodMorningService, "CHANNEL_GOOD_MORNING")
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
            notificationManager.notify(5, notificationGoodMorning)
        }
    }
}