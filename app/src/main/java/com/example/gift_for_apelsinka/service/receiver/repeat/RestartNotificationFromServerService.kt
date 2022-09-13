package com.example.gift_for_apelsinka.service.receiver.repeat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gift_for_apelsinka.service.NotificationFromServerService
import com.example.gift_for_apelsinka.service.receiver.background.NetworkChangeReceiver
import com.example.gift_for_apelsinka.util.WorkWithServices
import java.util.*

class RestartNotificationFromServerService : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        if(context == null) return

        if(NetworkChangeReceiver.isOnline(context)) {
            context.stopService(Intent(context, NotificationFromServerService::class.java))
            NotificationFromServerService.running = false
            NotificationFromServerService.isKillOS = false

            context.startService(Intent(context, NotificationFromServerService::class.java))
        }

        WorkWithServices.alarmTask(
            context,
            Calendar.getInstance().apply { set(Calendar.MINUTE, get(Calendar.MINUTE) + 16) },
            RestartNotificationFromServerService::class.java)
    }
}