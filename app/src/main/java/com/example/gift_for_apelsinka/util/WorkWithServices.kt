package com.example.gift_for_apelsinka.util

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.gift_for_apelsinka.service.GoodMorningService
import com.example.gift_for_apelsinka.service.LocationService
import com.example.gift_for_apelsinka.service.NotificationFromServerService
import com.example.gift_for_apelsinka.service.RandomQuestionService


object WorkWithServices {
    fun restartAllServices(context : Context) {
        if(!isServiceRunning(context, GoodMorningService::class.java))
            context.startService(Intent(context, GoodMorningService::class.java))

        if(!isServiceRunning(context, NotificationFromServerService::class.java))
            context.startService(Intent(context, NotificationFromServerService::class.java))

        if(!isServiceRunning(context, RandomQuestionService::class.java))
            context.startService(Intent(context, RandomQuestionService::class.java))

        if(!isServiceRunning(context, RandomQuestionService::class.java)) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                context.startService(Intent(context, LocationService::class.java))
            }

        }
    }
    private fun stopAllServices(context: Context) {
        context.stopService(Intent(context, GoodMorningService::class.java))
        context.stopService(Intent(context, NotificationFromServerService::class.java))
        context.stopService(Intent(context, RandomQuestionService::class.java))
        context.stopService(Intent(context, LocationService::class.java))
    }
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}