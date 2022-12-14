package com.example.gift_for_apelsinka.retrofit.network.repo

import com.google.gson.internal.LinkedTreeMap

interface MessageRepo {
    suspend fun sendMessage(who : Int, toWhom : Int, text: String) : LinkedTreeMap<*, *>
}