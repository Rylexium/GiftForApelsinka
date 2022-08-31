package com.example.gift_for_apelsinka.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.example.gift_for_apelsinka.util.dialogs.ShowToast
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


object IP {
    fun getIpv4(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()?.toString()
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("IP Address", ex.toString())
        }
        return null
    }

    var trying = false
    var connected = false
    fun isInternetAvailable(context : Context): Boolean {
        if(trying) return connected

        trying = true

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager? ?: return false
        val connected =
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED

        if(!connected) ShowToast.show(context, "Проверьте подключение к интернету")

        Handler(Looper.getMainLooper()).postDelayed({ trying = false }, 2_500)
        return connected
    }

}