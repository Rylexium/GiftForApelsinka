package com.example.gift_for_apelsinka.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.PowerManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.service.GoodMorningService
import com.example.gift_for_apelsinka.service.LocationService
import com.example.gift_for_apelsinka.service.NotificationFromServerService
import com.example.gift_for_apelsinka.service.RandomQuestionService


object WorkWithServices {
    fun startAllServices(context : Context) {
        if(!isServiceRunning(context, GoodMorningService::class.java))
            context.startService(Intent(context, GoodMorningService::class.java))

        if(!isServiceRunning(context, NotificationFromServerService::class.java))
            context.startService(Intent(context, NotificationFromServerService::class.java))

        if(!isServiceRunning(context, RandomQuestionService::class.java))
            context.startService(Intent(context, RandomQuestionService::class.java))

        if(!isServiceRunning(context, RandomQuestionService::class.java)) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                context.startService(Intent(context, LocationService::class.java))
            }

        }
    }
    private fun stopAllServices(context: Context) {
        context.stopService(Intent(context, GoodMorningService::class.java))
        context.stopService(Intent(context, NotificationFromServerService::class.java))
        context.stopService(Intent(context, RandomQuestionService::class.java))
        context.stopService(Intent(context, LocationService::class.java))
    }
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun restartService(context: Context, serviceClass : Class<*>) {
        val restartService = Intent(context, serviceClass)
        val pendingIntent = PendingIntent.getService(context, 1, restartService, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000] = pendingIntent
    }

    @SuppressLint("NewApi")
    fun createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID : String, channelName : String, context: Context): Notification {
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH)

        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        chan.enableVibration(true)

        val manager = (context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val hiddenChannel = NotificationChannel("Другое", "Другое", NotificationManager.IMPORTANCE_LOW)
        hiddenChannel.lightColor = Color.BLUE
        hiddenChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        hiddenChannel.enableVibration(false)
        manager.createNotificationChannel(hiddenChannel)

        val notificationBuilder = NotificationCompat.Builder(context, "Другое")
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setVibrate(longArrayOf(0))
            .setOnlyAlertOnce(true)
            .build()
        notification.flags = notification.flags or Notification.VISIBILITY_SECRET

        return notification
    }

    @SuppressLint("InvalidWakeLockTag")
    fun wakeUp(context: Context) {
        val pm = context.getSystemService(Service.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG")
        wakeLock.acquire(1_000L) //10*60*1000L = 10 minutes
        wakeLock.release()
    }

}