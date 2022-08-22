package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.retrofit.network.repo.MessageRepo
import com.google.gson.internal.LinkedTreeMap

object NetworkMessage : MessageRepo {
    override suspend fun sendMessage(text: String): LinkedTreeMap<*, *> {
        TODO("Not yet implemented")
    }
}