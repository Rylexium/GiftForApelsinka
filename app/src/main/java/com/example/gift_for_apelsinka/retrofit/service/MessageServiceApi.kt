package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.retrofit.requestmodel.RequestMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface MessageServiceApi {
    @POST("message")
    fun sendMessage(@Body messageText: RequestMessage) : Call<Any>
}