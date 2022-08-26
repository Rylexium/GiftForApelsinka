package com.example.gift_for_apelsinka.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkNotifications
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.Notifaction
import com.example.gift_for_apelsinka.util.WorkWithTime
import kotlinx.coroutines.*

class NotificationFromServerService : Service() {

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
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Thread {
            while (true) {
                 CoroutineScope(Dispatchers.IO).launch {
                     val notifications = NetworkNotifications.getNotifications()!!
                     for(notif in notifications) {

                         val image = if(notif.image != null) ConvertClass.convertStringToBitmap(notif.image) else R.drawable.mouse_of_apelsinka
                         val circleImage = InitView.getCircleImage(image, applicationContext)

                         val notification = NotificationCompat.Builder(applicationContext, "CHANNEL_NOTIFICATIONS_FROM_SERVER")
                             .setAutoCancel(true)
                             .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                             .setLargeIcon(circleImage)
                             .setWhen(notif.time ?: System.currentTimeMillis())
                             .setContentTitle(notif.title)
                             .setContentText(notif.text)
                             .setPriority(NotificationCompat.PRIORITY_HIGH)
                             .build()

                         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                             val notifChannel = NotificationChannel("CHANNEL_NOTIFICATIONS_FROM_SERVER", "CHANNEL_NOTIFICATIONS_FROM_SERVER", NotificationManager.IMPORTANCE_DEFAULT)
                             notificationManager.createNotificationChannel(notifChannel)
                         }
                         notificationManager.notify(1, notification)
                         NetworkNotifications.changeStatusNotification(notif.id)
                     }
                 }
                Thread.sleep(5_000)
            }
        }.start()
    }
}