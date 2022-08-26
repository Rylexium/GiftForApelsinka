package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.HandbookList
import retrofit2.Call
import retrofit2.http.*

interface HandbookServiceApi {
    @GET("handbook")
    fun getAllHandbook() : Call<HandbookList>

    @GET("handbook/{key}")
    fun getValueByKey(@Path("key") key : String) : Call<Handbook>

    @POST("handbook")
    fun postHandbook(@Body handbook: Handbook) : Call<Any>
}