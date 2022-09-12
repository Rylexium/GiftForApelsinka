package com.example.gift_for_apelsinka.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.gift_for_apelsinka.service.receiver.NotificationFromServerReceiver
import com.example.gift_for_apelsinka.service.socket.NotificationFromServerSocket
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.example.gift_for_apelsinka.util.WorkWithServices.alarmTaskRepeating
import com.example.gift_for_apelsinka.util.WorkWithServices.createChannelAndHiddenNotification
import java.util.concurrent.atomic.AtomicBoolean


class NotificationFromServerService : Service() {

    private val NOTIFICATION_CHANNEL_ID = "Канал уведомлений от сервера"
    private val channelName = "Канал уведомлений от сервера"

    companion object {
        private var running = AtomicBoolean(false)
    }

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("NotificationFromServerService", "onCreate")
        //alarmTaskRepeating(this, 960000, NotificationFromServerReceiver::class.java) //restart service every 16 minutes
        startMyOwnForeground()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val notification = createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID, channelName, this@NotificationFromServerService)
        startForeground(707, notification)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("NotificationFromServerService", "onStartCommand")
        initTask()
        return START_STICKY
    }
    private fun initTask() {
        if(!running.get()) {
            Log.e("NotificationFromServerService", "backgroundThreadStarted")
            NotificationFromServerSocket.initSocket(this)
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

        NotificationFromServerSocket.resetSubscriptions()
        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}