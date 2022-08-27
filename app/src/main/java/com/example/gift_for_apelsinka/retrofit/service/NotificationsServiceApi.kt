package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.retrofit.requestmodel.response.NotificationList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationsServiceApi {
    @GET("notifications")
    fun getNotifications(@Query("androidId") androidId : String) : Call<NotificationList>
}