package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotificationFromServerService : Service() {
    private var backgroundThread : Thread? = null
    private var channelId = 10

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("NotificationFromServerService", "onCreate")
        startMyOwnForeground()
        (this@NotificationFromServerService.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "CHANNEL_NOTIFICATIONS_FROM_SERVER"
        val channelName = "CHANNEL_NOTIFICATIONS_FROM_SERVER"
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
            .setContentTitle("NFS is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MAX)
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

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        initTask()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val restartIntent = Intent(applicationContext, NotificationFromServerService::class.java)

        val am = getSystemService(ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getService(this, 1, restartIntent, PendingIntent.FLAG_ONE_SHOT)
        am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi)
    }

    private suspend fun getCircleImage(notif : com.example.gift_for_apelsinka.retrofit.requestmodel.Notification): Bitmap {
        val image = if(notif.image != null) ConvertClass.convertStringToBitmap(notif.image) else {
            when((System.currentTimeMillis() % 5).toInt()) {
                1 -> R.drawable.mouse_of_apelsinka
                2 -> R.drawable.developer
                3 -> R.drawable.icon_of_developer
                else -> { R.drawable.oscar5 }
            }
        }
        return InitView.getCircleImage(image, this@NotificationFromServerService)
    }

    private suspend fun createNotification(notif: com.example.gift_for_apelsinka.retrofit.requestmodel.Notification): Notification {
        return NotificationCompat.Builder(this@NotificationFromServerService, "CHANNEL_NOTIFICATIONS_FROM_SERVER")
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
        return NotificationCompat.Builder(this@NotificationFromServerService, "CHANNEL_NOTIFICATIONS_FROM_SERVER")
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
        val notificationManager = this@NotificationFromServerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notifChannel = NotificationChannel("CHANNEL_NOTIFICATIONS_FROM_SERVER", "CHANNEL_NOTIFICATIONS_FROM_SERVER", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notifChannel)
        }
    }

    @SuppressLint("HardwareIds")
    private fun task() : Thread {
        return Thread {
            while (true) {
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
                        for(item in listNotification) {
                            notify(channelId, item)
                            channelId += 10
                        }
                        if(notifications.size != 1) notify(0, summaryNotification)
                    }
                }
                Thread.sleep(5_000)
            }
        }
    }
}
