package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.retrofit.model.StatementsList
import retrofit2.Call
import retrofit2.http.GET

interface StatementsServiceApi {
    @GET("statements")
    fun getStatements(): Call<StatementsList>
}