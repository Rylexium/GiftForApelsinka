package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.db.model.Statements
import com.example.gift_for_apelsinka.retrofit.CallbackWithRetry
import com.example.gift_for_apelsinka.retrofit.Services.statementsServiceApi
import com.example.gift_for_apelsinka.retrofit.network.repo.StatementsRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.StatementsList
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NetworkStatements : StatementsRepo {
    override suspend fun getStatements() : List<Statements> {
        return suspendCoroutine {
            val call: Call<StatementsList> = statementsServiceApi!!.getStatements()

            call.enqueue(object : CallbackWithRetry<StatementsList>(call) {
                override fun onResponse(call: Call<StatementsList>, response: Response<StatementsList>) {
                    it.resume(response.body()!!.getStatements())
                }

                override fun onFailure(call: Call<StatementsList>, t: Throwable) {
                }
            })
        }
    }
}