package com.example.gift_for_apelsinka.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gift_for_apelsinka.service.NotificationFromServerService
import com.example.gift_for_apelsinka.util.WorkWithServices


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        WorkWithServices.restartAllServices(context)
    }
}