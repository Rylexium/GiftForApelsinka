package com.example.gift_for_apelsinka.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.gift_for_apelsinka.util.WorkWithTime

class NotificationFromServerService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        task()
        return START_STICKY
    }

    private fun task() {
        Thread {
            while (true) {

                Thread.sleep(600_000)
            }
        }.start()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        task()
    }
}