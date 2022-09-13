package com.example.gift_for_apelsinka.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.gift_for_apelsinka.service.socket.NotificationFromServerSocket
import com.example.gift_for_apelsinka.util.WorkWithServices


class NotificationFromServerService : Service() {

    private var isRestart = true
    private var handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = Runnable { stopSelf() }
    companion object {
        var running = false
        var isKillOS = true
    }

    override fun onCreate() {
        Log.e("NotificationFromServerService", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("NotificationFromServerService", "onStartCommand")
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        if(!running) {
            Log.e("NotificationFromServerService", "backgroundThreadStarted")
            NotificationFromServerSocket.initSocket(this)
            running = true
            handler.postDelayed(runnable, 100_000)
        }
        else {
            isRestart = false
            stopSelf()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("NotificationFromServerService", "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.e("NotificationFromServerService", "onDestroy")

        if(isRestart) { //нужно рестартнуть
            if(isKillOS) { // убила ОС?
                NotificationFromServerSocket.resetSubscriptions()
                WorkWithServices.restartService(this, this.javaClass)
                running = false
                isKillOS = true
            }
        }
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}