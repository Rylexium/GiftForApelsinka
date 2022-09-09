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
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.cache.NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER
import com.example.gift_for_apelsinka.cache.channelNameNotificationFromServer
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkNotifications
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class NotificationFromServerReceiver : BroadcastReceiver() {
    private lateinit var context : Context
    private var channelId = 10

    private lateinit var notificationManager : NotificationManager

    @SuppressLint("HardwareIds", "UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        this.context = context
        notificationManager =  context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        CoroutineScope(Dispatchers.IO).launch {
            val notifications =
                NetworkNotifications
                    .getNotifications(
                        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)) ?: return@launch

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

            NotificationManagerCompat.from(context).apply {
                channelId = 10
                for(item in listNotification.toSet()) {
                    notify(channelId, item)
                    channelId += 10
                }
                if(notifications.size != 1) notify(0, summaryNotification)
            }
        }
    }



    private suspend fun getCircleImage(notif : com.example.gift_for_apelsinka.retrofit.requestmodel.Notification): Bitmap {
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

    private suspend fun createNotification(notif: com.example.gift_for_apelsinka.retrofit.requestmodel.Notification): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_baseline_wb_sunny_24)
            .setLargeIcon(getCircleImage(notif))
            .setWhen(notif.time ?: System.currentTimeMillis())
            .setContentTitle(notif.title)
            .setContentText(notif.text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notif.text))
            .setGroup("group")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
    }
    private fun createSummingNotification(listNotification : List<Notification>, listData : List<String>): Notification {
        val text = "${listNotification.size} " + if(listNotification.size == 1) "новое сообщение" else "новых сообщений"
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER)
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
            val notifChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID_NOTIFICATION_FROM_SERVER, channelNameNotificationFromServer, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notifChannel)
        }
    }
}