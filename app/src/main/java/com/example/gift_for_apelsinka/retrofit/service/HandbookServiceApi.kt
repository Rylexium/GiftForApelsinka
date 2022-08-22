package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.db.model.Handbook
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface HandbookServiceApi {
    @GET("handbook")
    fun getAllHandbook() : Call<List<Handbook>>

    @GET("handbook/{key}")
    fun getValueByKey(key : String)

    @POST("handbook")
    fun setValueByKey(key : String, value : String)
}