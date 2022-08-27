package com.example.gift_for_apelsinka.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkNotifications
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView
import kotlinx.coroutines.*

class NotificationFromServerService : Service() {
    private var channelId = 10
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        task()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        task()
    }
    private fun task() {
        val notificationManager = this@NotificationFromServerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Thread {
            while (true) {
                 CoroutineScope(Dispatchers.IO).launch {
                     val notifications = NetworkNotifications.getNotifications()!!
                     if(notifications.isEmpty()) return@launch
                     val listNotification : MutableList<Notification> = mutableListOf()
                     val listData : MutableList<String> = mutableListOf()
                     for(notif in notifications) {

                         val image = if(notif.image != null) ConvertClass.convertStringToBitmap(notif.image) else {
                             when((System.currentTimeMillis() % 5).toInt()) {
                                 1 -> R.drawable.mouse_of_apelsinka
                                 2 -> R.drawable.developer
                                 3 -> R.drawable.icon_of_developer
                                 else -> { R.drawable.oscar5 }
                             }
                         }
                         val circleImage = InitView.getCircleImage(image, this@NotificationFromServerService)

                         val notification = NotificationCompat.Builder(this@NotificationFromServerService, "CHANNEL_NOTIFICATIONS_FROM_SERVER")
                             .setAutoCancel(true)
                             .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                             .setLargeIcon(circleImage)
                             .setWhen(notif.time ?: System.currentTimeMillis())
                             .setContentTitle(notif.title)
                             .setContentText(notif.text)
                             .setGroup("group")
                             .setPriority(NotificationCompat.PRIORITY_HIGH)
                             .build()
                         listNotification.add(notification)
                         listData.add(notif.title + " " + notif.text)

                         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                             val notifChannel = NotificationChannel("CHANNEL_NOTIFICATIONS_FROM_SERVER", "CHANNEL_NOTIFICATIONS_FROM_SERVER", NotificationManager.IMPORTANCE_DEFAULT)
                             notificationManager.createNotificationChannel(notifChannel)
                         }

                         NetworkNotifications.changeStatusNotification(notif.id)
                     }

                     val summaryNotification = NotificationCompat.Builder(this@NotificationFromServerService, "CHANNEL_NOTIFICATIONS_FROM_SERVER")
                         .setContentTitle("Уведомления")
                         .setContentText("${listNotification.size} новых сообщений")
                         .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                         .setStyle(NotificationCompat.InboxStyle()
                             .setBigContentTitle("${listNotification.size} новых сообщений")
                             .setSummaryText("Вопросы").also {
                                 for(item in listData)
                                     it.addLine(item)
                             })
                         .setGroup("group")
                         .setGroupSummary(true)
                         .build()

                     NotificationManagerCompat.from(this@NotificationFromServerService).apply {
                         for(item in listNotification) {
                             notify(channelId, item)
                             item.defaults = item.defaults or Notification.DEFAULT_VIBRATE
                             item.defaults = item.defaults or Notification.DEFAULT_SOUND
                             channelId += 10
                         }
                         notify(0, summaryNotification)
                     }
                 }
                Thread.sleep(5_000)
            }
        }.start()
    }
}