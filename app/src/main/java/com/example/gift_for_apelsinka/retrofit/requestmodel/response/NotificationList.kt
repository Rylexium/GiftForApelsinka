package com.example.gift_for_apelsinka.retrofit.requestmodel.response

import com.example.gift_for_apelsinka.retrofit.requestmodel.Notification
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NotificationList {
    @SerializedName("notifications")
    @Expose
    private var notifications : List<Notification> = listOf()

    fun getNotifications() : List<Notification> = notifications
}