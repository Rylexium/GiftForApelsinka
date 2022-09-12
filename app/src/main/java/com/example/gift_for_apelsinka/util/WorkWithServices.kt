package com.example.gift_for_apelsinka.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.*
import com.example.gift_for_apelsinka.service.LocationService
import com.example.gift_for_apelsinka.service.NotificationFromServerService
import com.example.gift_for_apelsinka.service.receiver.GoodMorningReceiver
import com.example.gift_for_apelsinka.service.receiver.RandomQuestionReceiver
import com.example.gift_for_apelsinka.service.socket.NotificationFromServerSocket
import com.google.gson.Gson
import java.util.*


object WorkWithServices {

    fun startAllServices(context : Context) {
        val sharedPreferences = context.getSharedPreferences("preference_key", Context.MODE_PRIVATE)

        // смотрим есть ли что-то в shared если нет ИЛИ если есть и время меньше текущего, то запускаем
        if(sharedPreferences?.getString(GoodMorningReceiver.KEY_TIMETABLE, null) == null
            ||
            Gson().fromJson(sharedPreferences.getString(GoodMorningReceiver.KEY_TIMETABLE, Gson().toJson(Calendar.getInstance())), Calendar::class.java)
                .timeInMillis <= Calendar.getInstance().timeInMillis) {
            val timetable = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 11)
//                set(Calendar.MINUTE, 7)
                  set(Calendar.SECOND, get(Calendar.SECOND) + 3)
//                set(Calendar.MILLISECOND, 0)
            }
            sharedPreferences.edit().putString(GoodMorningReceiver.KEY_TIMETABLE, Gson().toJson(timetable)).apply()
            createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID_GOOD_MORNING, channelNameGoodMorning, context)
            alarmTask(context, timetable, GoodMorningReceiver::class.java)
        }

        if(sharedPreferences?.getString(RandomQuestionReceiver.KEY_TIMETABLE, null) == null
            ||
            Gson().fromJson(sharedPreferences.getString(RandomQuestionReceiver.KEY_TIMETABLE, Gson().toJson(Calendar.getInstance())), Calendar::class.java)
                .timeInMillis <= Calendar.getInstance().timeInMillis) {
            val timetable = Calendar.getInstance().apply {
//                set(Calendar.HOUR_OF_DAY, 11)
//                set(Calendar.MINUTE, 10)
                  set(Calendar.SECOND, get(Calendar.SECOND) + 5)
//                set(Calendar.MILLISECOND, 0)
            }
            sharedPreferences.edit().putString(RandomQuestionReceiver.KEY_TIMETABLE, Gson().toJson(timetable)).apply()
            createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID_RANDOM_QUESTION, channelNameRandomQuestion, context)
            alarmTask(context, timetable, RandomQuestionReceiver::class.java)
        }


        createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER, channelNameNotificationFromServer, context)
        // BroadCast запускает эту службу
//        if(!isServiceRunning(context, NotificationFromServerService::class.java))
//            context.startService(Intent(context, NotificationFromServerService::class.java))

        if(!isServiceRunning(context, LocationService::class.java)) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                context.startService(Intent(context, LocationService::class.java))
            }

        }
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

    private fun getPendingIntent(context: Context, receiver : Class<*>): PendingIntent? {
        return PendingIntent.getBroadcast(context, 0, Intent(context, receiver),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun alarmTask(context: Context, timetable : Calendar, receiver : Class<*>) {
        val alarmManager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val periodMillis = timetable.timeInMillis - Calendar.getInstance().timeInMillis

        val pendingIntent = getPendingIntent(context, receiver)
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + periodMillis, pendingIntent)
    }
    fun alarmTaskRepeating(context: Context, periodMillis : Long, receiver : Class<*>) {
        val alarmManager = context.getSystemService(Service.ALARM_SERVICE) as AlarmManager

        val pendingIntent = getPendingIntent(context, receiver)
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime(), periodMillis, pendingIntent)
    }
}