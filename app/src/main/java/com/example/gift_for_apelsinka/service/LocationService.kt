package com.example.gift_for_apelsinka.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import com.example.gift_for_apelsinka.util.WorkWithServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class LocationService : Service() {
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager
    private var locationByGps : Location? = null
    private var locationByNetwork : Location? = null
    private var hasNetwork : Boolean = false
    private var hasGps : Boolean = false

    companion object {
        private var backgroundWorkThread: Thread? = null
        private var backgroundInitThread: Thread? = null

        private var runningBackgroundWorkThread = AtomicBoolean(false)
        private var runningBackgroundInitThread = AtomicBoolean(false)
    }
    val NOTIFICATION_CHANNEL_ID = "Другое"
    val channelName = "Другое"
    private lateinit var notificationManager : NotificationManager

    @SuppressLint("NewApi")
    override fun onCreate() {
        Log.e("LocationService", "onCreate")
        notificationManager = this@LocationService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startMyOwnForeground()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        startForeground(4,
            WorkWithServices.createChannelAndHiddenNotification(NOTIFICATION_CHANNEL_ID, channelName, this@LocationService))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initTask()
        return START_STICKY
    }

    private fun initTask() {
        if(!runningBackgroundInitThread.get()) {
            backgroundInitThread = init()
            backgroundInitThread?.start()
        }
        if(!runningBackgroundWorkThread.get()) {
            backgroundWorkThread = work()
            backgroundWorkThread?.start()
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.e("LocationService", "onTaskRemoved")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        Log.e("LocationService", "onDestroy")

        runningBackgroundWorkThread.set(false)
        runningBackgroundInitThread.set(false)

        try {
            backgroundInitThread?.interrupt()
            backgroundWorkThread?.interrupt()
        } catch (e : java.lang.Exception) {}

        backgroundInitThread = null
        backgroundWorkThread = null

        stopSelf()
        WorkWithServices.restartService(this, this.javaClass)
        super.onDestroy()
    }

    private fun init(): Thread {
        return Thread {
            while (true) {
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
                try {
                    Thread.sleep(80_000) // 80_000
                } catch (e : java.lang.Exception) {}
            }
        }
    }
    private fun work() : Thread {
        return Thread {
            while (true) {
                try {
                    Thread.sleep(60_000) // 60_000
                } catch (e : java.lang.Exception) {}

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