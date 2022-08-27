package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkNotifications
import com.example.gift_for_apelsinka.util.ConvertClass
import com.example.gift_for_apelsinka.util.InitView
import kotlinx.coroutines.*
import java.security.AccessController.getContext


class NotificationFromServerService : Service() {

//    var mReceiver: BroadcastReceiver? = null
//    var countOn = 0
//    var countOff = 0
//
//    override fun onCreate() {
//        super.onCreate()
//        Log.e("UpdateService", "Started")
//        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
//        filter.addAction(Intent.ACTION_SCREEN_OFF)
//        filter.addAction(Intent.ACTION_ANSWER)
//        mReceiver = MyReceiver()
//        registerReceiver(mReceiver, filter)
//    }
//
//    override fun onDestroy() {
//        unregisterReceiver(mReceiver)
//        Log.e("onDestroy Reciever", "Called")
//        super.onDestroy()
//    }
//
//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        val screenOn = intent.getBooleanExtra("screen_state", false)
//        if (!screenOn) {
//            Log.e("screenON", "Called")
//            Log.e("viaService", "CountOn =$countOn")
//            Toast.makeText(applicationContext, "Awake", Toast.LENGTH_LONG)
//                .show()
//        } else {
//            Log.e("screenOFF", "Called")
//            Log.e("viaService", "CountOff =$countOff")
//        }
//        return START_STICKY
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
    class MyReceiver : BroadcastReceiver() {
        private var screenOff = false
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    screenOff = true
                }
                Intent.ACTION_SCREEN_ON -> {
                    screenOff = false
                }
                Intent.ACTION_ANSWER -> {
                }
            }
            val i = Intent(context, NotificationFromServerService::class.java)
            i.putExtra("screen_state", screenOff)
            context.startService(i)
        }
    }

    private var channelId = 10
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val wakeLock: PowerManager.WakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                    acquire()
                }
            }
        wakeLock.release()
        task()
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        task()
    }
    @SuppressLint("HardwareIds")
    private fun task() {
        val notificationManager = this@NotificationFromServerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Thread {
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
                         .setStyle(NotificationCompat.InboxStyle()
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
        }.start()
    }

}
