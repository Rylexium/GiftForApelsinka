package com.example.gift_for_apelsinka.retrofit.network.repo

import com.example.gift_for_apelsinka.retrofit.requestmodel.Notification
import com.google.gson.internal.LinkedTreeMap

interface NotificationsRepo {
    suspend fun getNotifications(): List<Notification>?
    suspend fun changeStatusNotification(id : Int): LinkedTreeMap<*, *>
}