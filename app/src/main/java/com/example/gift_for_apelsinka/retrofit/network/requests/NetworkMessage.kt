package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.retrofit.Services
import com.example.gift_for_apelsinka.retrofit.network.repo.MessageRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.MessageText
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object NetworkMessage : MessageRepo {
    override suspend fun sendMessage(text: String): LinkedTreeMap<*, *> {
        return suspendCoroutine {
            val call = Services.messageServiceApi?.sendMessage(MessageText(text))
            call?.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    println(response.body())
                    it.resume(response.body() as LinkedTreeMap<*, *>)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }
    }
}