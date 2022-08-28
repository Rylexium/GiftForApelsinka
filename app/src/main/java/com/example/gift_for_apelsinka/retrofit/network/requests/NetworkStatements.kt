package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.db.model.Statements
import com.example.gift_for_apelsinka.retrofit.CallbackWithRetry
import com.example.gift_for_apelsinka.retrofit.Services.statementsServiceApi
import com.example.gift_for_apelsinka.retrofit.network.repo.StatementsRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.statements.StatementsList
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NetworkStatements : StatementsRepo {
    override suspend fun getStatements(page : Int) : List<Statements> {
        return wrapper(statementsServiceApi!!.getStatements(page))
    }

    override suspend fun getStatements(page: Int, size: Int): List<Statements> {
        return wrapper(statementsServiceApi!!.getStatements(page, size))
    }

    private suspend fun wrapper(call : Call<StatementsList>) : List<Statements> {
        return suspendCoroutine {
            call.enqueue(object : CallbackWithRetry<StatementsList>(call) {
                override fun onResponse(call: Call<StatementsList>, response: Response<StatementsList>) {
                    it.resume(response.body()!!.getStatements() ?: listOf())
                }

                override fun onFailure(call: Call<StatementsList>, t: Throwable) {
                }
            })
        }
    }
}