package com.example.gift_for_apelsinka.retrofit.network

import com.example.gift_for_apelsinka.db.model.Statements
import com.example.gift_for_apelsinka.retrofit.RetrofitInstance
import com.example.gift_for_apelsinka.retrofit.Services.statementsServiceApi
import com.example.gift_for_apelsinka.retrofit.requestmodel.StatementsList
import com.example.gift_for_apelsinka.retrofit.service.StatementsServiceApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetworkStatements {
    suspend fun getStatements() : List<Statements> {
        return suspendCoroutine {
            val call: Call<StatementsList> = statementsServiceApi!!.getStatements()

            call.enqueue(object : Callback<StatementsList> {
                override fun onResponse(
                    call: Call<StatementsList>,
                    response: Response<StatementsList>) {
                    it.resume(response.body()!!.getStatements())
                }

                override fun onFailure(call: Call<StatementsList>, t: Throwable) {
                    it.resumeWithException(t)
                }
            })
        }
    }
}