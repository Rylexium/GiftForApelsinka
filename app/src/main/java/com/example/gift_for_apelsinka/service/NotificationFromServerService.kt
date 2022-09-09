package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.gift_for_apelsinka.service.receiver.NotificationFromServerReceiver
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.example.gift_for_apelsinka.util.WorkWithServices.createChannelAndHiddenNotification
import java.util.concurrent.atomic.AtomicBoolean


class NotificationFromServerService : Service() {

    private val DELAY = 10_000L
    private val NOTIFICATION_CHANNEL_ID = "Канал уведомлений от сервера"
    private val channelName = "Канал уведомлений от сервера"

    private lateinit var notificationManager : NotificationManager
    private var receiver: BroadcastReceiver? = null

    companion object {
        private var backgroundThread: Thread? = null
        private var running = AtomicBoolean(false)
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("NotificationFromServerService", "onCreate")
        notificationManager = this@NotificationFromServerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        receiver = NotificationFromServerReceiver()
        registerReceiver(receiver, IntentFilter().apply {
            addAction("android.intent.action.BOOT_COMPLETED")
            addAction("android.intent.action.NOTIFY")
            addAction("scan_notifications")
        })
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
    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("NotificationFromServerService", "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }
    override fun onDestroy() {
        Log.e("NotificationFromServerService", "onDestroy")
        running.set(false)

        try {
            backgroundThread?.interrupt()
        } catch (e : Exception) {}
        backgroundThread = null

        if(receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
        }

        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
        super.onDestroy()
    }

    @SuppressLint("HardwareIds")
    private fun task() : Thread {
        return Thread {
            while (true) {
                sendBroadcast(Intent("scan_notifications"))
                try {
                    Thread.sleep(DELAY)
                } catch (e : java.lang.Exception){}
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}