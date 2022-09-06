package com.example.gift_for_apelsinka.service
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkNotifications
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.example.gift_for_apelsinka.util.WorkWithServices.createChannelAndHiddenNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean


class NotificationFromServerService : Service() {
    private var channelId = 10
    companion object {
        private var backgroundThread: Thread? = null
        private var running = AtomicBoolean(false)
    }
    private val NOTIFICATION_CHANNEL_ID = "Канал уведомлений от сервера"
    private val channelName = "Канал уведомлений от сервера"

    private lateinit var notificationManager : NotificationManager

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("NotificationFromServerService", "onCreate")
        notificationManager = this@NotificationFromServerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startMyOwnForeground()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        startForeground(3,
            createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID, channelName, this@NotificationFromServerService))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("NotificationFromServerService", "onStartCommand")
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        if(!running.get()) {
            Log.e("NotificationFromServerService", "backgroundThreadStarted")
            running.set(true)
            backgroundThread = task()
            backgroundThread?.start()
        }
        else stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.e("NotificationFromServerService", "onDestroy")
        running.set(true)
        try {
            backgroundThread?.interrupt()
        } catch (e : Exception) {}
        backgroundThread = null
        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
        super.onDestroy()
    }
    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("NotificationFromServerService", "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
        running.set(true)
        try {
            backgroundThread?.interrupt()
        } catch (e : Exception) {}
        backgroundThread = null
        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
    }

    private suspend fun getCircleImage(notif : com.example.gift_for_apelsinka.retrofit.requestmodel.Notification): Bitmap {
        val image = if(notif.image != null) ConvertClass.convertStringToBitmap(notif.image) else {
            when(java.util.Random().nextInt(5)) {
                1 -> R.drawable.mouse_of_apelsinka
                2 -> R.drawable.developer
                3 -> R.drawable.icon_of_developer
                else -> { R.drawable.oscar5 }
            }
        }
        return InitView.getCircleImage(image, this@NotificationFromServerService)
    }

    private suspend fun createNotification(notif: com.example.gift_for_apelsinka.retrofit.requestmodel.Notification): Notification {
        return NotificationCompat.Builder(this@NotificationFromServerService, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setLargeIcon(getCircleImage(notif))
            .setWhen(notif.time ?: System.currentTimeMillis())
            .setContentTitle(notif.title)
            .setContentText(notif.text)
            .setGroup("group")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }
    private fun createSummingNotification(listNotification : List<Notification>, listData : List<String>): Notification {
        val text = "${listNotification.size} " + if(listNotification.size == 1) "новое сообщение" else "новых сообщений"
        return NotificationCompat.Builder(this@NotificationFromServerService, NOTIFICATION_CHANNEL_ID)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle(text)
                    .setSummaryText("Вопросы").also {
                        for(item in listData)
                            it.addLine(item)
                    })
            .setGroup("group")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setGroupSummary(true)
            .build()
    }
    private fun notifForSdkO() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notifChannel)
        }
    }

    @SuppressLint("HardwareIds")
    private fun task() : Thread {
        return Thread {
            while (running.get()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val notifications =
                        NetworkNotifications
                            .getNotifications(
                                Settings.Secure.getString(this@NotificationFromServerService.contentResolver, Settings.Secure.ANDROID_ID)) ?: return@launch

                    if(notifications.isEmpty()) return@launch

                    val listNotification : MutableList<Notification> = mutableListOf()
                    val listData : MutableList<String> = mutableListOf()
                    for(notif in notifications) {
                        val notification = createNotification(notif)

                        listNotification.add(notification)
                        listData.add(notif.title + " " + notif.text)

                        notifForSdkO()
                    }

                    val summaryNotification = createSummingNotification(listNotification, listData)

                    NotificationManagerCompat.from(this@NotificationFromServerService).apply {
                        channelId = 10
                        for(item in listNotification.toSet()) {
                            notify(channelId, item)
                            channelId += 10
                        }
                        if(notifications.size != 1) notify(0, summaryNotification)
                    }
                }
                try {
                    Thread.sleep(5_000)
                }catch (e : java.lang.Exception){}
            }
        }
    }
}