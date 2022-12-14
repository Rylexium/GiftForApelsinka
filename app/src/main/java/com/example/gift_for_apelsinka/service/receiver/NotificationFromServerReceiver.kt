package com.example.gift_for_apelsinka.service.receiver

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER
import com.example.gift_for_apelsinka.cache.channelNameNotificationFromServer
import com.example.gift_for_apelsinka.service.NotificationFromServerService
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView
import com.example.gift_for_apelsinka.util.WorkWithServices
import java.util.*


class NotificationFromServerReceiver : BroadcastReceiver() {
    @SuppressLint("HardwareIds", "UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        context.stopService(Intent(context, NotificationFromServerService::class.java)) //stop servie
        WorkWithServices.restartService(context, NotificationFromServerService::class.java) //restart service
    }
    companion object {
        suspend fun showNotifications(notifications: List<com.example.gift_for_apelsinka.retrofit.requestmodel.Notification>,
                                      notificationManager: NotificationManager,
                                      context: Context) {
            if(notifications.isEmpty()) return
            val listNotification : MutableList<Pair<Int, Notification>> = mutableListOf()
            val listData : MutableList<String> = mutableListOf()
            for(notif in notifications) {
                val notification = createNotification(notif, context)

                listNotification.add(Pair(notif.id, notification))
                listData.add(notif.title + " " + notif.text)

                notifForSdkO(notificationManager)
            }

            val summaryNotification = createSummingNotification(listNotification.map { it.second }, listData, context)

            NotificationManagerCompat.from(context).apply {
                for(item in listNotification.toSet()) {
                    notify(item.first, item.second)
                }
                if(notifications.size != 1) notify(listNotification.sumOf { it.first }, summaryNotification)
            }
        }
        private suspend fun getCircleImage(notif : com.example.gift_for_apelsinka.retrofit.requestmodel.Notification, context: Context): Bitmap {
            val image = if(notif.image != null) ConvertClass.convertStringToBitmap(notif.image)
            else {
                when(Random().nextInt(5)) {
                    1 -> R.drawable.mouse_of_apelsinka
                    2 -> R.drawable.developer
                    3 -> R.drawable.icon_of_developer
                    else -> { R.drawable.oscar5 }
                }
            }
            return InitView.getCircleImage(image, context)
        }

        private suspend fun createNotification(notif: com.example.gift_for_apelsinka.retrofit.requestmodel.Notification, context: Context): Notification {
            return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setLargeIcon(getCircleImage(notif, context))
                .setWhen(notif.time ?: System.currentTimeMillis())
                .setContentTitle(notif.title)
                .setContentText(notif.text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(notif.text))
                .setGroup("group")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
        }
        private fun createSummingNotification(listNotification : List<Notification>, listData : List<String>, context: Context): Notification {
            val text = "${listNotification.size} " + if(listNotification.size == 1) "?????????? ??????????????????" else "?????????? ??????????????????"
            return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .setBigContentTitle(text)
                        .setSummaryText("??????????????").also {
                            for(item in listData)
                                it.addLine(item)
                        })
                .setGroup("group")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setGroupSummary(true)
                .build()
        }
        private fun notifForSdkO(notificationManager : NotificationManager) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER, channelNameNotificationFromServer, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(notifChannel)
            }
        }
    }
}