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
import com.example.gift_for_apelsinka.util.Notifaction.generateTextOfEquation
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class RandomQuestionService : Service() {
    private val KEY_TIMETABLE = "EquationRandomTimetable"
    private val KEY_TEXT = "EquationRandomText"

    private val defaultHour = 1
    private val defaultMinute = 38
    private val DELAY = 120_000L //millisecond
    private val DELAY_FOR_NEXT_NOTIFICATION = 25 //minute
    private lateinit var wakeLock: PowerManager.WakeLock

    val NOTIFICATION_CHANNEL_ID = "Канал случайных вопросов"
    val channelName = "Канал случайных вопросов"

    companion object {
        private var backgroundThread: Thread? = null
        private var running = AtomicBoolean(false)
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("RandomQuestionService", "onCreate")
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::RandomQuestionService")
            }
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
    }

    private fun equationNotification(text : String) {
        val notificationManager = this@RandomQuestionService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notifChannel)
        }
        notificationManager.notify(6, getNotification(text))
    }

    private fun task() : Thread {
        val sharedPreferences = getSharedPreferences("preference_key", Context.MODE_PRIVATE)
        val timetable = Gson().fromJson(sharedPreferences.getString(KEY_TIMETABLE, Gson().toJson(
            Calendar.getInstance())), Calendar::class.java)
        timetable.set(Calendar.HOUR_OF_DAY, defaultHour)
        timetable.set(Calendar.MINUTE, defaultMinute)
        return Thread {
            while (running.get()) {
                wakeLock.acquire()

                val nowCalendar = Calendar.getInstance()

                if(nowCalendar >= timetable) {

                    var text = sharedPreferences.getString(KEY_TEXT, generateTextOfEquation())
                    equationNotification(text.toString())

                    CoroutineScope(Dispatchers.IO).launch {
                        //timetable.set(Calendar.DAY_OF_YEAR, nowCalendar.get(Calendar.DAY_OF_YEAR) + 1)
                        timetable.set(Calendar.HOUR_OF_DAY, nowCalendar.get(Calendar.HOUR_OF_DAY))
                        timetable.set(Calendar.MINUTE, nowCalendar.get(Calendar.MINUTE) + DELAY_FOR_NEXT_NOTIFICATION)

                        val previousText = "Текущие случайный вопрос : $text"

                        text = generateTextOfEquation()

                        NetworkMessage.sendMessage(2, 2, "$previousText\nСледующий случайный вопрос : " +
                                "${timetable.get(Calendar.HOUR_OF_DAY)} : ${timetable.get(Calendar.MINUTE)}, $text")

                        val timetableJson = Gson().toJson(timetable)
                        sharedPreferences.edit()
                            .putString(KEY_TIMETABLE, timetableJson)
                            .putString(KEY_TEXT, text)
                            .apply()
                    }

                    try {
                        wakeLock.release()
                        Thread.sleep(DELAY) // 5 минуты
                    } catch (e : java.lang.Exception){}
                }
            }
        }
    }

    private fun getNotification(text : String) = runBlocking {
        val id = when(Random().nextInt(3)) {
            0 -> R.drawable.developer
            1 -> R.drawable.icon_of_developer
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
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}