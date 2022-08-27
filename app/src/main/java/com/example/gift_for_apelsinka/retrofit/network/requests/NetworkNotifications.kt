package com.example.gift_for_apelsinka.retrofit.network.requests


import com.example.gift_for_apelsinka.retrofit.CallbackWithRetry
import com.example.gift_for_apelsinka.retrofit.Services
import com.example.gift_for_apelsinka.retrofit.network.repo.NotificationsRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.Notification
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.NotificationList
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NetworkNotifications : NotificationsRepo {
    override suspend fun getNotifications(androidId : String) : List<Notification>? {
        return suspendCoroutine {
            val call = Services.notificationsServiceApi?.getNotifications(androidId)
            call?.enqueue(object : CallbackWithRetry<NotificationList>(call) {
                override fun onResponse(call: Call<NotificationList>, response: Response<NotificationList>) {
                    it.resume(response.body()?.getNotifications())
                }

                override fun onFailure(call: Call<NotificationList>, t: Throwable) {
                }
            })
        }
    }
}