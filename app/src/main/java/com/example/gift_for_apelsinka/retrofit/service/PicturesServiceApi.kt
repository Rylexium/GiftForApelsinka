package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.db.model.FieldPhoto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface PicturesServiceApi {
    @GET("pictures")
    fun getAllPictures(): Call<List<FieldPhoto>>

    @POST("pictures")
    fun setTitlePicture(id : Int, newTitle : String)

    @GET("pictures/apelsinka")
    fun getAllApelsinkaPicture() : Call<List<FieldPhoto>>

    @GET("pictures/oscar")
    fun getAllOscarPicture() : Call<List<FieldPhoto>>

    @GET("pictures/lera")
    fun getAllLeraPicture() : Call<List<FieldPhoto>>

    @GET("pictures/rylexium")
    fun getAllRylexiumPicture() : Call<List<FieldPhoto>>

    @GET("pictures/main")
    fun getAllMainPicture() : Call<List<FieldPhoto>>

    @GET("pictures/logo")
    fun getAllLogoPicture() : Call<List<FieldPhoto>>

}