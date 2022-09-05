package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
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
import com.example.gift_for_apelsinka.util.Notifaction.generateTextOfEquation
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.example.gift_for_apelsinka.util.WorkWithTime
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean


class RandomQuestionService : Service() {
    private val KEY_HOUR = "EquationRandomHour"
    private val KEY_MINUTE = "EquationRandomMinute"
    private val KEY_TEXT = "EquationRandomText"

    private var backgroundThread : Thread? = null
    private val running: AtomicBoolean = AtomicBoolean(false)
    private var flagSending = AtomicBoolean(false)

    val NOTIFICATION_CHANNEL_ID = "Канал случайных вопросов"
    val channelName = "Канал случайных вопросов"

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("RandomQuestionService", "onCreate")
        startMyOwnForeground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        startForeground(2,
            WorkWithServices.createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID, channelName, this@RandomQuestionService))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("RandomQuestionService", "onStartCommand")
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        if(!running.get()) {
            Log.e("RandomQuestionService", "backgroundThreadStarted")
            running.set(true)
            backgroundThread = task()
            backgroundThread?.start()
        } else stopSelf()
    }

    override fun onDestroy() {
        Log.e("RandomQuestionService", "onDestroy")
        running.set(false)
        try {
            backgroundThread?.interrupt()
        } catch (e : Exception) {}
        backgroundThread = null
        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("RandomQuestionService", "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        running.set(false)
        try {
            backgroundThread?.interrupt()
        } catch (e : Exception) {}
        backgroundThread = null
        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
    }

    private fun equationNotification(text : String) {
        val notificationManager = this@RandomQuestionService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notifChannel)
        }
        notificationManager.notify(6, getNotification(text))
    }

    private fun task() : Thread {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        var randomHour = sharedPreferences.getInt(KEY_HOUR, 18)
        var randomMinute = sharedPreferences.getInt(KEY_MINUTE, 30)

        return Thread {
            while (running.get()) {

                val nowHour = WorkWithTime.getNowHour()
                val nowMinute = WorkWithTime.getNowMinute()

                if((nowHour * 60 + nowMinute) >= (randomHour * 60 + randomMinute)) {
                    if(flagSending.get()) continue
                    flagSending.set(true)

                    var text = sharedPreferences.getString(KEY_TEXT, generateTextOfEquation())
                    equationNotification(text.toString())

                    CoroutineScope(Dispatchers.IO).launch {
                        val totalMinute = ((nowHour * 60) + nowMinute) + 45

                        randomHour = totalMinute / 60
                        randomMinute = totalMinute % 60

                        val previousText = "\nТекущие $text"

                        text = generateTextOfEquation()

                        NetworkMessage.sendMessage(2, 2, "Следующий случайный вопрос : $randomHour : $randomMinute, $text + $previousText")
                        sharedPreferences.edit()
                            .putInt(KEY_HOUR, randomHour)
                            .putInt(KEY_MINUTE, randomMinute)
                            .putString(KEY_TEXT, text)
                            .apply()
                        flagSending.set(false)
                    }
                    try {
                        Thread.sleep(120_000) // 5 минуты
                    }catch (e : java.lang.Exception){}
                }
//                    var max = 16
//                    var min = 23
//                    randomHour = java.util.Random().nextInt(((max - min) + 1) + min)
//
//                    max = 59
//                    min = 0
//                    randomMinute = java.util.Random().nextInt(((max - min) + 1) + min)
//                    randomMinute = (System.currentTimeMillis() % 59).toInt()
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        NetworkMessage.sendMessage(2, 2, "Случайный вопрос : $randomHour : $randomMinute")
//                    }
//
//                    sharedPreferences.edit()
//                        .putInt(KEY_HOUR, randomHour)
//                        .putInt(KEY_MINUTE, randomMinute)
//                        .apply()
//
//                    Thread.sleep(18_000_000) // на 5 часов засыпаем
//              }
            }
        }
    }

    private fun getNotification(text : String) = runBlocking {
        val id = when((1..3).random()) {
            1 -> R.drawable.developer
            2 -> R.drawable.icon_of_developer
            else -> { R.drawable.lexa1 }
        }
        val circleImage = async { InitView.getCircleImage(id, this@RandomQuestionService) }
        return@runBlocking NotificationCompat.Builder(this@RandomQuestionService, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setLargeIcon(circleImage.await())
            .setWhen(System.currentTimeMillis())
            .setContentTitle("Вопросик")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}