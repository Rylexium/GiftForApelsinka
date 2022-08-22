package com.example.gift_for_apelsinka.retrofit.service

import retrofit2.Call
import retrofit2.http.POST

interface MessageServiceApi {
    @POST("message")
    fun sendMessage(text : String) : Call<Any>
}