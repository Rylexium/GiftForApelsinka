package com.example.gift_for_apelsinka.service.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.example.gift_for_apelsinka.service.NotificationFromServerService


class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context == null) return

        try {
            if (isOnline(context)) {
                context.stopService(Intent(context, NotificationFromServerService::class.java))
                context.startService(Intent(context, NotificationFromServerService::class.java))
            }
            else
                context.stopService(Intent(context, NotificationFromServerService::class.java))
        } catch (e: java.lang.NullPointerException) {
            e.printStackTrace()
        }
    }


    companion object {
        fun isOnline(context: Context): Boolean {
            return try {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                //should check null because in airplane mode it will be null
                netInfo != null && netInfo.isConnected
            } catch (e: NullPointerException) {
                e.printStackTrace()
                false
            }
        }
    }
}