package com.example.gift_for_apelsinka.retrofit.network.repo

import com.example.gift_for_apelsinka.retrofit.requestmodel.Notification
import com.example.gift_for_apelsinka.retrofit.requestmodel.NotificationDelivered
import com.google.gson.internal.LinkedTreeMap

interface NotificationsRepo {
    suspend fun getNotifications(androidId : String): List<Notification>?
    suspend fun notificationDelivered(notificationDelivered: NotificationDelivered) : LinkedTreeMap<*, *>
}