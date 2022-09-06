package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithServices
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

    private val defaultHour = 1
    private val defaultMinute = 37
    private val DELAY = 60_000L //millisecond
    private val DELAY_FOR_NEXT_NOTIFICATION = 30 //minute

    private lateinit var notificationManager: NotificationManager
    private lateinit var wakeLock: PowerManager.WakeLock

    companion object {
        private var backgroundThread: Thread? = null
        private var running = AtomicBoolean(false)
        private var flagSending = AtomicBoolean(false)
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("GoodMorningService", "onCreate")
        notificationManager = this@GoodMorningService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::GoodMorningService")
        }
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
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


    @SuppressLint("InvalidWakeLockTag")
    private fun taskGoodMorning() : Thread {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)

        val timetable = Gson().fromJson(sharedPreferences.getString(KEY_TIMETABLE, Gson().toJson(Calendar.getInstance())), Calendar::class.java)
        timetable.set(Calendar.HOUR_OF_DAY, defaultHour)
        timetable.set(Calendar.MINUTE, defaultMinute)

        return Thread {
            while (running.get()) {

                wakeLock.acquire()
                val nowCalendar = Calendar.getInstance()

                if(nowCalendar >= timetable) {

                    var title = sharedPreferences.getString(KEY_TITLE, Notifaction.generateTitleOfGoodMorning())
                    var text = sharedPreferences.getString(KEY_TEXT, Notifaction.generateTextOfGoodMorning())
                    goodMorningNotification(title.toString(), text.toString())

                    CoroutineScope(Dispatchers.IO).launch {

                        //timetable.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR) + 1)
                        timetable.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY))
                        timetable.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE) + DELAY_FOR_NEXT_NOTIFICATION)

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
                    wakeLock.release()
                    Thread.sleep(DELAY)
                } catch (e : java.lang.Exception){}
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
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notifChannel)
            }
            notificationManager.notify(5, notificationGoodMorning)
        }
    }
}