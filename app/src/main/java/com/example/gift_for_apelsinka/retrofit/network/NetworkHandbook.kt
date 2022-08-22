package com.example.gift_for_apelsinka.retrofit.network

import com.example.gift_for_apelsinka.db.model.Handbook
import com.example.gift_for_apelsinka.retrofit.Services.handbookServiceApi
import com.example.gift_for_apelsinka.retrofit.requestmodel.HandbookList
import com.google.gson.internal.LinkedTreeMap
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetworkHandbook  {

    suspend fun getHandbook() : List<Handbook> {
        return suspendCoroutine {
            val call = handbookServiceApi!!.getAllHandbook()
            call.enqueue(object : Callback<HandbookList> {
                override fun onResponse(
                    call: Call<HandbookList>,
                    response: Response<HandbookList>) {
                    it.resume(response.body()!!.getHandbook())
                }

                override fun onFailure(call: Call<HandbookList>, t: Throwable) {
                    it.resumeWithException(t)
                }

            })
        }
    }

    suspend fun getValueByKey(key : String) : Handbook? {
        return suspendCoroutine {
            val call = handbookServiceApi!!.getValueByKey(key)
            call.enqueue(object : Callback<Handbook> {
                override fun onResponse(call: Call<Handbook>, response: Response<Handbook>) {
                    it.resume(response.body())
                }

                override fun onFailure(call: Call<Handbook>, t: Throwable) {
                    it.resumeWithException(t)
                }

            })
        }
    }

    suspend fun postHandbook(key: String, value : String) : LinkedTreeMap<*, *> {
        return suspendCoroutine {
            val call = handbookServiceApi?.postHandbook(Handbook(key, value))
            call?.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    it.resume(response.body() as LinkedTreeMap<*, *>)
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            })
        }
    }

}