package com.example.gift_for_apelsinka.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gift_for_apelsinka.service.NotificationFromServerService


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val intentService = Intent(context, NotificationFromServerService::class.java)
        context.startService(intentService)
    }
}