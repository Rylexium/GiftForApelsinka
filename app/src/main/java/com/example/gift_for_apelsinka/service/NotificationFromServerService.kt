package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.service.receiver.NotificationFromServerReceiver
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.example.gift_for_apelsinka.util.WorkWithServices.createChannelAndHiddenNotification
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean


class NotificationFromServerService : Service() {

    private val DELAY = 5_000L
    private val NOTIFICATION_CHANNEL_ID = "Канал уведомлений от сервера"
    private val channelName = "Канал уведомлений от сервера"

    private lateinit var notificationManager : NotificationManager
    private var receiver: BroadcastReceiver? = null

    companion object {
        private var thread : HandlerThread? = null
        private var handler : Handler? = null
        private lateinit var runnable : Runnable
        private var running = AtomicBoolean(false)
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("NotificationFromServerService", "onCreate")
        notificationManager = this@NotificationFromServerService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startMyOwnForeground()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val notification = createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID, channelName, this@NotificationFromServerService)
        startForeground(3, notification)
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

            receiver = NotificationFromServerReceiver()
            registerReceiver(receiver, IntentFilter().apply {
                addAction("android.intent.action.BOOT_COMPLETED")
                addAction("android.intent.action.NOTIFY")
                addAction("scan_notifications")
            })

            thread = HandlerThread("threadHandler")
            thread!!.isDaemon = true
            thread?.start()
            handler = Handler(thread!!.looper)
            runnable = Runnable {
                sendBroadcast(Intent("scan_notifications"))
                handler?.postDelayed(runnable, DELAY)
            }
            handler?.postDelayed(runnable, 0)

            Handler(Looper.getMainLooper()).postDelayed({
                stopSelf()
            }, 65_000L)
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

        handler?.removeCallbacks(runnable)
        try {
            thread?.quitSafely()
            thread = null
        } catch (e : Exception) {}
        handler = null
        if(receiver != null) {
            unregisterReceiver(receiver)
            receiver = null
        }

        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}