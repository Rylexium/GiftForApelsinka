package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.retrofit.CallbackWithRetry
import com.example.gift_for_apelsinka.retrofit.Services
import com.example.gift_for_apelsinka.retrofit.network.repo.MessageRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.RequestMessage
import com.example.gift_for_apelsinka.util.IP
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object NetworkMessage : MessageRepo {
    override suspend fun sendMessage(who : Int, toWhom : Int, text: String): LinkedTreeMap<*, *> {
        return suspendCoroutine {
            val call = Services.messageServiceApi?.sendMessage(RequestMessage(IP.getIpv4().toString(), who,toWhom, text))
            call?.enqueue(object : CallbackWithRetry<Any>(call) {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    println(response.body())
                    it.resume(response.body() as LinkedTreeMap<*, *>)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            })
        }
    }
}