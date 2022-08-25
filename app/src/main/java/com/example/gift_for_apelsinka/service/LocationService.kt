package com.example.gift_for_apelsinka.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat

class LocationService : Service() {
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager
    private lateinit var locationByGps : Location
    private lateinit var locationByNetwork : Location
    private var hasNetwork : Boolean = false
    private var hasGps : Boolean = false


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        init()
        Thread {
            while (true) {
                work()
                Thread.sleep(5_000)
            }
        }.start()
        return START_STICKY
    }

    private fun init() {
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

        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }
        val lastKnownLocationByNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let {
            locationByNetwork = lastKnownLocationByNetwork
        }
    }
    private fun work() {
        if (locationByGps.accuracy > locationByNetwork.accuracy) {
            currentLocation = locationByGps
            Handler(Looper.getMainLooper()).post {
//                Toast.makeText(
//                    this,
//                    currentLocation!!.latitude.toString() + " " + currentLocation!!.longitude.toString(),
//                    Toast.LENGTH_LONG
//                ).show()
            }
        } else {
            currentLocation = locationByNetwork
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}