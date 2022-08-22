package com.example.gift_for_apelsinka.retrofit.service

import com.example.gift_for_apelsinka.retrofit.requestmodel.FieldPhotoList
import com.example.gift_for_apelsinka.retrofit.requestmodel.NewTitleById
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface PicturesServiceApi {
    @GET("pictures")
    fun getAllPictures(): Call<FieldPhotoList>

    @POST("pictures")
    fun setTitlePicture(newTitleById: NewTitleById) : Call<Any>

    @GET("pictures/apelsinka")
    fun getAllApelsinkaPicture() : Call<FieldPhotoList>

    @GET("pictures/oscar")
    fun getAllOscarPicture() : Call<FieldPhotoList>

    @GET("pictures/lera")
    fun getAllLeraPicture() : Call<FieldPhotoList>

    @GET("pictures/rylexium")
    fun getAllRylexiumPicture() : Call<FieldPhotoList>

    @GET("pictures/main")
    fun getAllMainPicture() : Call<FieldPhotoList>

    @GET("pictures/logo")
    fun getAllLogoPicture() : Call<FieldPhotoList>

}