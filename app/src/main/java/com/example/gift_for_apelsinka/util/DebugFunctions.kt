package com.example.gift_for_apelsinka.util

import com.example.gift_for_apelsinka.cache.androidId
import com.example.gift_for_apelsinka.retrofit.network.requests.NetworkMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DebugFunctions {
    private var resultDebug = "\n\n"
    fun sendReport() {
        CoroutineScope(Dispatchers.IO).launch {
            NetworkMessage.sendMessage(0, 0, "$androidId" + "\n$resultDebug")
        }
    }
    fun addDebug(where : String, text: String) {
        resultDebug += "$where : $text\n"
    }
}