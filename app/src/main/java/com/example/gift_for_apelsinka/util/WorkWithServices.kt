package com.example.gift_for_apelsinka.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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

    fun restartService(applicationContext : Context, serviceClass : Class<*>) {
        val restartService = Intent(applicationContext, serviceClass)
        val pendingIntent = PendingIntent.getService(applicationContext, 1, restartService, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = applicationContext.getSystemService(Service.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME, 5000] = pendingIntent
    }
    @SuppressLint("NewApi")
    fun createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID : String, channelName : String, context: Context): Notification {
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.BLUE
        chan.enableVibration(true)
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager = (context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.icon_of_developer)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        notification.flags = notification.flags or Notification.VISIBILITY_SECRET
        return notification
    }
}