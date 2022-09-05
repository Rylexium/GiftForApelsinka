package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
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
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class GoodMorningService : Service() {
    private val KEY_TIMETABLE = "GoodMorningRandomTimetable"

    private val KEY_TITLE = "GoodMorningRandomTitle"
    private val KEY_TEXT = "GoodMorningRandomText"

    private val NOTIFICATION_CHANNEL_ID = "Канал доброго утра"
    private val channelName = "Канал доброго утра"
    private lateinit var notificationManager : NotificationManager

    private var backgroundThread : Thread? = null
    private var running = AtomicBoolean(false)
    private var flagSending = AtomicBoolean(false)

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("GoodMorningService", "onCreate")
        notificationManager = this@GoodMorningService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startMyOwnForeground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        startForeground(1,
            WorkWithServices.createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID, channelName, this@GoodMorningService))
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("GoodMorningService", "onStartCommand")
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        if(!running.get()) {
            Log.e("GoodMorningService", "backgroundThreadStarted")
            running.set(true)
            backgroundThread = taskGoodMorning()
            backgroundThread?.start()
        }
        else stopSelf()
    }

    override fun onDestroy() {
        Log.e("GoodMorningService", "onDestroy")
        running.set(false)
        try {
            backgroundThread?.interrupt()
        } catch (e : Exception) {}
        backgroundThread = null
        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) { // onBackPressed и из диспетчера кик проц
        Log.e("GoodMorningService", "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        running.set(false)
        try {
            backgroundThread?.interrupt()
        } catch (e : Exception) {}
        backgroundThread = null
        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    private fun taskGoodMorning() : Thread {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)

        val timetable = Gson().fromJson(sharedPreferences.getString(KEY_TIMETABLE, Gson().toJson(Calendar.getInstance())), Calendar::class.java)
        timetable.set(Calendar.HOUR_OF_DAY, 8)
        timetable.set(Calendar.MINUTE, 58)

        return Thread {
            while (running.get()) {

                val nowCalendar = Calendar.getInstance()

                if(nowCalendar >= timetable) {
                    if(flagSending.get()) continue
                    flagSending.set(true)

                    var title = sharedPreferences.getString(KEY_TITLE, Notifaction.generateTitleOfGoodMorning())
                    var text = sharedPreferences.getString(KEY_TEXT, Notifaction.generateTextOfGoodMorning())
                    goodMorningNotification(title.toString(), text.toString())

                    CoroutineScope(Dispatchers.IO).launch {

                        //timetable.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR) + 1)
                        timetable.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY))
                        timetable.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE) + 30)

                        val previous = "Текущие доброе утро : $title : $text"

                        title = Notifaction.generateTitleOfGoodMorning()
                        text = Notifaction.generateTextOfGoodMorning()

                        NetworkMessage.sendMessage(2, 2,
                            "$previous\nСледующие доброе утро : ${timetable.get(Calendar.HOUR_OF_DAY)} : ${timetable.get(Calendar.MINUTE)}, $title : $text")
                        val timetableJson = Gson().toJson(timetable)
                        sharedPreferences.edit()
                            .putString(KEY_TIMETABLE, timetableJson)
                            .putString(KEY_TITLE, title)
                            .putString(KEY_TEXT, text)
                            .apply()

                        flagSending.set(false)
                    }
                }
                try {
                    Thread.sleep(300_000) // 10 минут = 600_000
                }catch (e : java.lang.Exception){}
//                goodMorningNotification() //debug
//
//                val nowHour = getNowHour()
//                val nowMinute = getNowMinute()
//                if((nowHour * 60 + nowMinute) >= (randomHour * 60 + randomMinute)) {
//                    goodMorningNotification()
//
//                    var max = 12
//                    var min = 8
//                    randomHour = java.util.Random().nextInt(((max - min) + 1) + min)
//
//                    max = 59
//                    min = 0
//                    randomMinute = java.util.Random().nextInt(((max - min) + 1) + min)
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        NetworkMessage.sendMessage(2, 2, "Доброе утро : $randomHour : $randomMinute")
//                    }
//
//                    sharedPreferences.edit()
//                        .putInt(KEY_HOUR, randomHour)
//                        .putInt(KEY_MINUTE, randomMinute)
//                        .apply()
//
//                    Thread.sleep(61_200_000) // на 17 часов засыпаем
//                }
//                Thread.sleep(600_000) // 10 минут
            }
        }
    }

    private fun goodMorningNotification(title : String, text : String) {
        CoroutineScope(Dispatchers.IO).launch {
            val circleImageApelsinka = InitView.getCircleImage(R.drawable.mouse_of_apelsinka, this@GoodMorningService)
            val notificationGoodMorning = NotificationCompat.Builder(this@GoodMorningService, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setLargeIcon(circleImageApelsinka)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(text)
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