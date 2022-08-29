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
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction.generateTextOfEquation
import com.example.gift_for_apelsinka.util.WorkWithTime
import kotlinx.coroutines.*

class RandomQuestionService : Service() {
    private val KEY_HOUR = "EquationRandomHour"
    private val KEY_MINUTE = "EquationRandomMinute"

    private var backgroundThread : Thread? = null

    @SuppressLint("NewApi")
    override fun onCreate() {
        startMyOwnForeground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "CHANNEL_QUESTION"
        val channelName = "CHANNEL_QUESTION"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.icon_of_developer)
            .setContentTitle("RQ is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        notification.flags = notification.flags or Notification.VISIBILITY_SECRET
        startForeground(3, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        backgroundThread = task()
        backgroundThread?.start()
    }

    override fun onDestroy() {
        initTask()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val restartIntent = Intent(applicationContext, NotificationFromServerService::class.java)

        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getService(this, 1, restartIntent, PendingIntent.FLAG_ONE_SHOT);
        am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
    }

    private fun equationNotification() {
        val notificationManager = this@RandomQuestionService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel("CHANNEL_QUESTION", "CHANNEL_QUESTION", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notifChannel)
        }
        notificationManager.notify(4, getNotification())
    }

    private fun task() : Thread {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        var randomHour = sharedPreferences.getInt(KEY_HOUR, 20)
        var randomMinute = sharedPreferences.getInt(KEY_MINUTE, 28)
        return Thread {
            while (true) {
                val nowHour = WorkWithTime.getNowHour()
                val nowMinute = WorkWithTime.getNowMinute()
                if(nowHour == randomHour && nowMinute >= randomMinute) {
                    equationNotification()
                    randomHour = (16..23).random()
                    randomMinute = (System.currentTimeMillis() % 59).toInt()
                    sharedPreferences.edit()
                        .putInt(KEY_HOUR, randomHour)
                        .putInt(KEY_MINUTE, randomMinute)
                        .apply()
                }
                Thread.sleep(60_000)
            }
        }
    }

    private fun getNotification() = runBlocking {
        val id = when((1..3).random()) {
            1 -> R.drawable.developer
            2 -> R.drawable.icon_of_developer
            else -> { R.drawable.lexa1 }
        }
        val circleImage = async { InitView.getCircleImage(id, this@RandomQuestionService) }
        return@runBlocking NotificationCompat.Builder(this@RandomQuestionService, "CHANNEL_QUESTION")
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setLargeIcon(circleImage.await())
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Вопросик")
            .setContentText(generateTextOfEquation())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}