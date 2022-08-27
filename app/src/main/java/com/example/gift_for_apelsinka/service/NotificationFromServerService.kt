package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.os.IBinder
import android.provider.Settings
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
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTask()
        return START_STICKY
    }
    private fun initTask() {
        backgroundThread?.stop()
        backgroundThread = task()
        backgroundThread?.start()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        backgroundThread?.stop()
        backgroundThread = task()
        backgroundThread?.start()
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
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()
    }
    private fun createSummingNotification(listNotification : List<Notification>, listData : List<String>): Notification {
        return NotificationCompat.Builder(this@NotificationFromServerService, "CHANNEL_NOTIFICATIONS_FROM_SERVER")
                .setContentTitle("Уведомления")
                .setContentText("${listNotification.size} новых сообщений")
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setBigContentTitle("${listNotification.size} новых сообщений")
                        .setSummaryText("Вопросы").also {
                            for(item in listData)
                                it.addLine(item)
                        })
                .setGroup("group")
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
                        notify(0, summaryNotification)
                    }
                }
                Thread.sleep(5_000)
            }
        }
    }
}
