package com.example.gift_for_apelsinka.retrofit

import com.example.gift_for_apelsinka.cache.HEROKU_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = HEROKU_URL
    private var retrofit: Retrofit? = null
    fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}