package com.example.gift_for_apelsinka.service.receiver.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gift_for_apelsinka.service.socket.NotificationFromServerSocket
import com.example.gift_for_apelsinka.util.WorkWithServices
import com.example.gift_for_apelsinka.util.dialogs.ShowToast


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        WorkWithServices.startAllServices(context)
    }
}