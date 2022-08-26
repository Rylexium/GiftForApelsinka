package com.example.gift_for_apelsinka.retrofit.network.requests

import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.retrofit.CallbackWithRetry
import com.example.gift_for_apelsinka.retrofit.Services.handbookServiceApi
import com.example.gift_for_apelsinka.retrofit.network.repo.HandbookRepo
import com.example.gift_for_apelsinka.retrofit.requestmodel.response.HandbookList
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object NetworkHandbook : HandbookRepo {

    override suspend fun getHandbook() : MutableMap<String, String> {
        return suspendCoroutine {
            val call = handbookServiceApi!!.getAllHandbook()
            call.enqueue(object : CallbackWithRetry<HandbookList>(call) {
                override fun onResponse(call: Call<HandbookList>, response: Response<HandbookList>) {
                    val res = mutableMapOf<String, String>()
                    for(item in response.body()!!.getHandbook())
                        res[item.key] = item.value
                    it.resume(res)
                }

                override fun onFailure(call: Call<HandbookList>, t: Throwable) {
                }
            })
        }
    }

    override suspend fun getValueByKey(key : String) : Handbook? {
        return suspendCoroutine {
            val call = handbookServiceApi!!.getValueByKey(key)
            call.enqueue(object : CallbackWithRetry<Handbook>(call) {
                override fun onResponse(call: Call<Handbook>, response: Response<Handbook>) {
                    it.resume(response.body())
                }

                override fun onFailure(call: Call<Handbook>, t: Throwable) {
                }

            })
        }
    }

    override suspend fun postHandbook(key: String, value : String) : LinkedTreeMap<*, *> {
        return suspendCoroutine {
            val call = handbookServiceApi?.postHandbook(Handbook(key, value))
            call?.enqueue(object : CallbackWithRetry<Any>(call) {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    it.resume(response.body() as LinkedTreeMap<*, *>)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            })
        }
    }

}