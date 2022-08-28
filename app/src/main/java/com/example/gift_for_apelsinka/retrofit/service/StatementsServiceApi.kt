package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.retrofit.requestmodel.response.statements.StatementsList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StatementsServiceApi {
    @GET("statements")
    fun getStatements(@Query("page") page : Int, @Query("size") size : Int): Call<StatementsList>

    @GET("statements")
    fun getStatements(@Query("page") page : Int): Call<StatementsList>
}