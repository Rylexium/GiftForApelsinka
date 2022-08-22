package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.db.model.Statements
import retrofit2.Call
import retrofit2.http.GET

interface StatementsServiceApi {
    @GET("statements")
    fun getStatements(): Call<List<Statements>>
}