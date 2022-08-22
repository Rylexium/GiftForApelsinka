package com.example.gift_for_apelsinka.retrofit

import com.example.gift_for_apelsinka.retrofit.service.HandbookServiceApi
import com.example.gift_for_apelsinka.retrofit.service.MessageServiceApi
import com.example.gift_for_apelsinka.retrofit.service.PicturesServiceApi
import com.example.gift_for_apelsinka.retrofit.service.StatementsServiceApi

object Services {
    var handbookServiceApi = RetrofitInstance.getRetrofit()?.create(HandbookServiceApi::class.java)
    var statementsServiceApi = RetrofitInstance.getRetrofit()?.create(StatementsServiceApi::class.java)
    var picturesServiceApi = RetrofitInstance.getRetrofit()?.create(PicturesServiceApi::class.java)
    var messageServiceApi = RetrofitInstance.getRetrofit()?.create(MessageServiceApi::class.java)
}