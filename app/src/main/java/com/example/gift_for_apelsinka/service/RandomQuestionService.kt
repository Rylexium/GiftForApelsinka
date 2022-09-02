package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
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


class RandomQuestionService : Service() {
    private val KEY_HOUR = "EquationRandomHour"
    private val KEY_MINUTE = "EquationRandomMinute"
    private val KEY_TEXT = "EquationRandomText"

    private var backgroundThread : Thread? = null
    private var killThread = false
    private var flagSending = false

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
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        killThread = false
        backgroundThread = task()
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
        killThread = true
        WorkWithServices.restartService(this, this.javaClass)
        WorkWithServices.startAllServices(this)
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
        var randomMinute = sharedPreferences.getInt(KEY_MINUTE, 40)

        return Thread {
            while (true) {
                if(killThread) break

                val nowHour = WorkWithTime.getNowHour()
                val nowMinute = WorkWithTime.getNowMinute()

                if((nowHour * 60 + nowMinute) >= (randomHour * 60 + randomMinute)) {
                    if(flagSending) continue
                    flagSending = true

                    var text = sharedPreferences.getString(KEY_TEXT, generateTextOfEquation())
                    equationNotification(text.toString())

                    CoroutineScope(Dispatchers.IO).launch {
                        val totalMinute = ((nowHour * 60) + nowMinute) + 45

                        randomHour = totalMinute / 60
                        randomMinute = totalMinute % 60

                        text = generateTextOfEquation()

                        NetworkMessage.sendMessage(2, 2, "Случайный вопрос : $randomHour : $randomMinute, $text")
                        sharedPreferences.edit()
                            .putInt(KEY_HOUR, randomHour)
                            .putInt(KEY_MINUTE, randomMinute)
                            .putString(KEY_TEXT, text)
                            .apply()
                        flagSending = false
                    }
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
                Thread.sleep(300_000) // 5 минуты
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