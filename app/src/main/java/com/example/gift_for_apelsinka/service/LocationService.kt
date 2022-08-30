package com.example.gift_for_apelsinka.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.gift_for_apelsinka.R
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import com.example.gift_for_apelsinka.util.WorkWithServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationService : Service() {
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager
    private var locationByGps : Location? = null
    private var locationByNetwork : Location? = null
    private var hasNetwork : Boolean = false
    private var hasGps : Boolean = false

    private var backgroundWorkThread : Thread? = null
    private var backgroundInitThread : Thread? = null
    private var killWorkThread = false
    private var killInitThread = false

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("LocationService", "onCreate")
        startMyOwnForeground()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "CHANNEL_GOOD_MORNING"
        val channelName = "CHANNEL_GOOD_MORNING"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.drawable.icon_of_developer)
            .setContentTitle("LS is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        notification.flags = notification.flags or Notification.VISIBILITY_SECRET
        startForeground(4, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        backgroundInitThread = init()
        backgroundWorkThread = work()

        backgroundInitThread?.start()
        backgroundWorkThread?.start()
        killWorkThread = false
        killInitThread = false
    }

    override fun onDestroy() {
        killWorkThread = true
        killInitThread = true
        stopSelf()
        WorkWithServices.restartAllServices(this@LocationService)
    }

    private fun init(): Thread {
        return Thread {
            while (true) {
                if(killInitThread) break
                try {
                    locationByGps = null
                    locationByNetwork = null
                    Log.e(Thread.currentThread().id.toString(), locationByGps.toString())
                    Log.e(Thread.currentThread().id.toString(), locationByNetwork.toString())
                    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

                    hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    val gpsLocationListener = LocationListener { location -> locationByGps = location }

                    hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    val networkLocationListener = LocationListener { location -> locationByNetwork = location }

                    if (hasGps) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, gpsLocationListener)
                        }
                    }
                    if (hasNetwork)
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, networkLocationListener)

                    val lastKnownLocationByGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    lastKnownLocationByGps?.let {
                        locationByGps = lastKnownLocationByGps
                    }

                    val lastKnownLocationByNetwork =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    lastKnownLocationByNetwork?.let {
                        locationByNetwork = lastKnownLocationByNetwork
                    }
                } catch (e : Exception) { }
                Thread.sleep(180_000) // 180_000
            }
        }
    }
    private fun work() : Thread {
        return Thread {
            init()
            while (true) {
                if(killWorkThread) break
                Thread.sleep(60_000) // 60_000

                Log.e(Thread.currentThread().id.toString(), locationByGps.toString())
                Log.e(Thread.currentThread().id.toString(), locationByNetwork.toString())
                if(locationByGps == null && locationByNetwork == null) continue

                if(locationByGps == null)
                    currentLocation = locationByNetwork
                else if(locationByNetwork == null)
                    currentLocation = locationByGps
                else if(locationByGps != null && locationByNetwork != null)
                    currentLocation = if (locationByGps?.accuracy!! > locationByNetwork?.accuracy!!) locationByGps else locationByNetwork

                val text = "GPS : " + currentLocation!!.latitude.toString() + " " + currentLocation!!.longitude.toString()
                CoroutineScope(Dispatchers.IO).launch { NetworkMessage.sendMessage(1, 1, text) }
            }
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}