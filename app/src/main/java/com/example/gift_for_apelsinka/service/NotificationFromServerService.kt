package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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
    class NotificationReceiver : BroadcastReceiver() {
        private var screenOff = false
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    screenOff = true
                }
                Intent.ACTION_SCREEN_ON -> {
                    screenOff = false
                }
            }
            val i = Intent(context, NotificationFromServerService::class.java)
            i.putExtra("screen_state", screenOff)
            context.startService(i)
        }
    }

    private var backgroundThread : Thread? = null

    private var channelId = 10
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val wakeLock: PowerManager.WakeLock =
//            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
//                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
//                    acquire()
//                }
//            }
//        wakeLock.release()
        backgroundThread = task()
        backgroundThread?.start()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        backgroundThread?.stop()
        backgroundThread = task()
        backgroundThread?.start()
    }
    @SuppressLint("HardwareIds")
    private fun task() : Thread {
        val notificationManager = this@NotificationFromServerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return Thread {
            while (true) {
                CoroutineScope(Dispatchers.IO).launch {
                    val notifications = NetworkNotifications.getNotifications(
                        Settings.Secure.getString(this@NotificationFromServerService.contentResolver, Settings.Secure.ANDROID_ID))
                        ?: return@launch
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

                    }

                    val summaryNotification = NotificationCompat.Builder(this@NotificationFromServerService, "CHANNEL_NOTIFICATIONS_FROM_SERVER")
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

                    NotificationManagerCompat.from(this@NotificationFromServerService).apply { for(item in listNotification) {
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
